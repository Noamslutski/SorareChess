package com.fantasychess.hpt.federation;

import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.data.entity.GameRecord;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Best-effort parser for the Israeli Chess Federation player page
 * (https://www.chess.org.il/Players/Player.aspx?Id=...).
 *
 * The federation site is ASP.NET WebForms with no JSON API, so player data is read
 * from the HTML. Labels on the page are Hebrew (דירוג / מהיר / בזק / מועדון / תאריך לידה).
 * This parser keys on those labels and on the games table; selectors are locked down by
 * {@code PlayerPageParserTest} against a saved fixture and may need tuning against the
 * live DOM on first run.
 */
public final class PlayerPageParser {

    private static final Pattern YEAR = Pattern.compile("(\\d{4})");

    private PlayerPageParser() { }

    public static ParsedPlayer parse(int playerId, String html) {
        Document doc = Jsoup.parse(html);
        ChessPlayer p = new ChessPlayer();
        p.playerId = playerId;
        p.club = "הפועל פתח תקווה";

        // Name: prefer an element explicitly marked, else the page <h1>/<title>.
        String name = textOf(doc, "[data-field=playerName], .player-name, h1");
        if (isBlank(name)) {
            name = doc.title();
            if (name != null && name.contains("/")) {
                name = name.substring(0, name.indexOf('/')).trim();
            }
        }
        if (!isBlank(name)) p.name = name.trim();

        // Label/value pairs anywhere on the page.
        p.ratingStandard = intNearLabel(doc, "דירוג", "תקני", "סטנדרט");
        p.ratingRapid = intNearLabel(doc, "מהיר");
        p.ratingBlitz = intNearLabel(doc, "בזק");
        p.title = firstNonBlank(textNearLabel(doc, "תואר"), "");
        p.fideId = textNearLabel(doc, "FIDE");

        String birth = textNearLabel(doc, "לידה", "שנתון");
        if (birth != null) {
            Matcher m = YEAR.matcher(birth);
            if (m.find()) p.birthYear = Integer.parseInt(m.group(1));
        }

        String gender = textNearLabel(doc, "מין", "מגדר");
        if (gender != null && (gender.contains("נקב") || gender.contains("בת") || gender.contains("F"))) {
            p.gender = "F";
        } else {
            p.gender = "M";
        }

        ParsedPlayer result = new ParsedPlayer(p);
        parseGames(doc, playerId, result);
        return result;
    }

    private static void parseGames(Document doc, int playerId, ParsedPlayer result) {
        // Look for a table whose header mentions a game-ish Hebrew column (יריב/תוצאה/תאריך).
        for (Element table : doc.select("table")) {
            String head = table.select("th, thead").text();
            boolean looksLikeGames = head.contains("תוצאה") || head.contains("יריב")
                    || head.contains("תאריך");
            if (!looksLikeGames) continue;

            for (Element row : table.select("tbody tr, tr")) {
                Elements cells = row.select("td");
                if (cells.size() < 3) continue;
                GameRecord g = new GameRecord();
                g.playerId = playerId;
                g.date = cells.get(0).text().trim();
                g.event = cells.size() > 1 ? cells.get(1).text().trim() : "";
                g.opponent = cells.size() > 2 ? cells.get(2).text().trim() : "";
                g.color = cells.size() > 3 ? normalizeColor(cells.get(3).text()) : "";
                g.result = cells.size() > 4 ? cells.get(4).text().trim() : "";
                if (!isBlank(g.opponent)) result.games.add(g);
            }
            if (!result.games.isEmpty()) break;
        }
    }

    private static String normalizeColor(String raw) {
        if (raw == null) return "";
        raw = raw.trim();
        if (raw.contains("לבן") || raw.equalsIgnoreCase("W")) return "W";
        if (raw.contains("שחור") || raw.equalsIgnoreCase("B")) return "B";
        return raw;
    }

    // --- helpers -----------------------------------------------------------

    private static String textOf(Document doc, String cssQuery) {
        Element e = doc.selectFirst(cssQuery);
        return e == null ? null : e.text();
    }

    /** Finds a label cell/element, returns the text of its sibling/next cell. */
    private static String textNearLabel(Document doc, String... labels) {
        for (String label : labels) {
            Elements matches = doc.getElementsContainingOwnText(label);
            for (Element el : matches) {
                // table row: <td>label</td><td>value</td>
                Element parentCell = el.closest("td, th");
                if (parentCell != null) {
                    Element next = parentCell.nextElementSibling();
                    if (next != null && !isBlank(next.text())) return next.text().trim();
                }
                // generic: next sibling element
                Element sib = el.nextElementSibling();
                if (sib != null && !isBlank(sib.text())) return sib.text().trim();
            }
        }
        return null;
    }

    private static int intNearLabel(Document doc, String... labels) {
        String t = textNearLabel(doc, labels);
        if (t == null) return 0;
        Matcher m = Pattern.compile("(\\d{3,4})").matcher(t);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String firstNonBlank(String a, String b) {
        return isBlank(a) ? b : a;
    }
}
