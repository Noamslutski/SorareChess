import { chromium } from 'playwright';
import { readFileSync, writeFileSync } from 'fs';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';

const here = dirname(fileURLToPath(import.meta.url));

// Inject the SAME bundled snapshot the Android app ships with.
const snapshot = JSON.parse(
  readFileSync(join(here, '../app/src/main/assets/petah_tikva_players.json'), 'utf8')
);
const html = readFileSync(join(here, 'index.html'), 'utf8')
  .replace('JSON_PLACEHOLDER', JSON.stringify(snapshot.players));

const tmp = join(here, '_rendered.html');
writeFileSync(tmp, html);

const browser = await chromium.launch({
  executablePath: process.env.PW_CHROMIUM || '/opt/pw-browsers/chromium/chrome-linux/chrome',
});
const page = await browser.newPage({ deviceScaleFactor: 2 });
await page.setViewportSize({ width: 1740, height: 820 });
await page.goto('file://' + tmp);
await page.waitForTimeout(400);
await page.screenshot({ path: join(here, 'fantasy_chess_mock.png'), fullPage: true });
await browser.close();
console.log('wrote design-mock/fantasy_chess_mock.png');
