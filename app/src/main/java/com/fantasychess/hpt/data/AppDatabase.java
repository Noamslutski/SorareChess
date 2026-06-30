package com.fantasychess.hpt.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.fantasychess.hpt.data.dao.GameRecordDao;
import com.fantasychess.hpt.data.dao.OwnedCardDao;
import com.fantasychess.hpt.data.dao.PackGrantDao;
import com.fantasychess.hpt.data.dao.PlayerDao;
import com.fantasychess.hpt.data.dao.SquadDao;
import com.fantasychess.hpt.data.dao.UserDao;
import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.data.entity.GameRecord;
import com.fantasychess.hpt.data.entity.OwnedCard;
import com.fantasychess.hpt.data.entity.PackGrant;
import com.fantasychess.hpt.data.entity.SquadSlot;
import com.fantasychess.hpt.data.entity.User;

@Database(
        entities = {User.class, ChessPlayer.class, OwnedCard.class, SquadSlot.class,
                PackGrant.class, GameRecord.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract PlayerDao playerDao();
    public abstract OwnedCardDao ownedCardDao();
    public abstract SquadDao squadDao();
    public abstract PackGrantDao packGrantDao();
    public abstract GameRecordDao gameRecordDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class, "fantasy_chess.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
