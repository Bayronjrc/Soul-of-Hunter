package com.by.soh.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.by.soh.database.DatabaseContract.HeroTemplates;
import com.by.soh.database.GameDatabaseHelper;
import com.by.soh.models.Hero;


import java.util.ArrayList;
import java.util.List;

public class HeroCrud {

    private static final String TAG = "HeroTemplateDAO";
    private GameDatabaseHelper dbHelper;

    public HeroCrud(Context context) {
        this.dbHelper = GameDatabaseHelper.getInstance(context); // Usando el Singleton
    }

    // ========== HELPER METHODS ==========

    /**
     * Convierte un objeto Hero (que representa un HeroTemplate) a ContentValues
     * para la tabla HeroTemplates.
     */
    private ContentValues templateToContentValues(Hero heroTemplate) {
        ContentValues values = new ContentValues();

        values.put(HeroTemplates.COLUMN_NAME, heroTemplate.getName());
        values.put(HeroTemplates.COLUMN_FACTION, heroTemplate.getFaction()); // Asumiendo que getFaction() devuelve el int
        values.put(HeroTemplates.COLUMN_ATTRIBUTE, heroTemplate.getAttribute());
        values.put(HeroTemplates.COLUMN_ROLE, heroTemplate.getRole());
        values.put(HeroTemplates.COLUMN_RARITY, heroTemplate.getRarity());

        values.put(HeroTemplates.COLUMN_BASE_HP, heroTemplate.getBaseHp());
        values.put(HeroTemplates.COLUMN_BASE_ATK, heroTemplate.getBaseAtk());
        values.put(HeroTemplates.COLUMN_BASE_DEF, heroTemplate.getBaseDef());
        values.put(HeroTemplates.COLUMN_BASE_DEF_MAGIC, heroTemplate.getBaseMagicDef());
        values.put(HeroTemplates.COLUMN_BASE_SPEED, heroTemplate.getBaseSpeed());

        return values;
    }

    /**
     * Convierte un Cursor de la tabla HeroTemplates a un objeto Hero (representando un HeroTemplate).
     */
    private Hero cursorToTemplate(Cursor cursor) {

        String name = cursor.getString(cursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_NAME));
        int faction = cursor.getInt(cursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_FACTION));
        int attribute = cursor.getInt(cursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_ATTRIBUTE));
        int role = cursor.getInt(cursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_ROLE));
        int rarity = cursor.getInt(cursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_RARITY));


        Hero heroTemplate = new Hero(name, faction, attribute, role, rarity);

        // El _ID de la tabla HeroTemplates
        heroTemplate.setId(cursor.getLong(cursor.getColumnIndexOrThrow(HeroTemplates._ID)));

        heroTemplate.setBaseHp(cursor.getInt(cursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_BASE_HP)));
        heroTemplate.setBaseAtk(cursor.getInt(cursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_BASE_ATK)));
        heroTemplate.setBaseDef(cursor.getInt(cursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_BASE_DEF)));
        heroTemplate.setBaseMagicDef(cursor.getInt(cursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_BASE_DEF_MAGIC)));
        heroTemplate.setBaseSpeed(cursor.getInt(cursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_BASE_SPEED)));

        return heroTemplate;
    }

    // ========== CREATE ==========


    public long insertHeroTemplate(Hero heroTemplate, String heroStringId) {
        SQLiteDatabase db = null;
        long newRowId = -1;

        if (heroStringId == null || heroStringId.isEmpty()) {
            Log.e(TAG, "Hero String ID (COLUMN_HERO_ID) es requerido para insertar un HeroTemplate.");
            return -1;
        }

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = templateToContentValues(heroTemplate);
            values.put(HeroTemplates.COLUMN_HERO_ID, heroStringId); // Asegurar que el ID único esté presente


            newRowId = db.insertWithOnConflict(HeroTemplates.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            // Usamos CONFLICT_IGNORE para que si el heroStringId ya existe, no falle ruidosamente,
            // pero newRowId será -1. Podrías usar CONFLICT_REPLACE si quieres actualizar si existe,
            // o CONFLICT_FAIL para que lance una excepción.

            if (newRowId != -1) {
                heroTemplate.setId(newRowId); // Actualiza el _ID del objeto
                Log.d(TAG, "HeroTemplate insertado: " + heroTemplate.getName() + " (StringID: " + heroStringId + ", _ID: " + newRowId + ")");
            } else {
                // Esto puede suceder si el heroStringId ya existe y usaste CONFLICT_IGNORE o si hubo otro error.
                Cursor checkCursor = db.query(HeroTemplates.TABLE_NAME, new String[]{HeroTemplates._ID},
                        HeroTemplates.COLUMN_HERO_ID + " = ?", new String[]{heroStringId}, null, null, null);
                if (checkCursor != null && checkCursor.moveToFirst()) {
                    Log.w(TAG, "Fallo al insertar HeroTemplate (heroStringId '" + heroStringId + "' ya existe o hubo otro error). _ID existente: " + checkCursor.getLong(0));
                } else {
                    Log.e(TAG, "Fallo al insertar HeroTemplate (heroStringId '" + heroStringId + "').");
                }
                if (checkCursor != null) checkCursor.close();
            }

        } catch (SQLiteException e) {
            Log.e(TAG, "Error insertando HeroTemplate " + heroTemplate.getName() + ": " + e.getMessage(), e);
        } finally {
            // Singleton: no cerrar db aquí
        }
        return newRowId;
    }

    // ========== READ ==========

    public Hero getHeroTemplateByRowId(long templateRowId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Hero heroTemplate = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = HeroTemplates._ID + " = ?";
            String[] selectionArgs = {String.valueOf(templateRowId)};

            cursor = db.query(
                    HeroTemplates.TABLE_NAME,
                    null, // Todas las columnas
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                heroTemplate = cursorToTemplate(cursor);
            }

        } catch (SQLiteException e) {
            Log.e(TAG, "Error obteniendo HeroTemplate por RowID " + templateRowId + ": " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error procesando cursor para HeroTemplate RowID " + templateRowId + ": " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return heroTemplate;
    }

    public Hero getHeroTemplateByStringId(String heroStringId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Hero heroTemplate = null;

        if (heroStringId == null || heroStringId.isEmpty()) {
            return null;
        }

        try {
            db = dbHelper.getReadableDatabase();
            String selection = HeroTemplates.COLUMN_HERO_ID + " = ?";
            String[] selectionArgs = {heroStringId};

            cursor = db.query(
                    HeroTemplates.TABLE_NAME,
                    null, // Todas las columnas
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                heroTemplate = cursorToTemplate(cursor);
            }

        } catch (SQLiteException e) {
            Log.e(TAG, "Error obteniendo HeroTemplate por StringID " + heroStringId + ": " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error procesando cursor para HeroTemplate StringID " + heroStringId + ": " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return heroTemplate;
    }


    public List<Hero> getAllHeroTemplates() {
        List<Hero> templates = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String orderBy = HeroTemplates.COLUMN_RARITY + " DESC, " + HeroTemplates.COLUMN_NAME + " ASC";

            cursor = db.query(
                    HeroTemplates.TABLE_NAME,
                    null, // Todas las columnas
                    null, null, null, null,
                    orderBy
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        Hero template = cursorToTemplate(cursor);
                        templates.add(template);
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, "Error procesando un HeroTemplate en getAllHeroTemplates: " + e.getMessage() + ". Omitiendo.", e);
                    }
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "Recuperados " + templates.size() + " HeroTemplates.");

        } catch (SQLiteException e) {
            Log.e(TAG, "Error obteniendo todos los HeroTemplates: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return templates;
    }

    // ========== UPDATE ==========

    public int updateHeroTemplate(Hero heroTemplate, String heroStringId) {
        if (heroStringId == null || heroStringId.isEmpty()) {
            Log.e(TAG, "No se puede actualizar HeroTemplate sin un StringID válido.");
            return 0;
        }

        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = templateToContentValues(heroTemplate);

            String selection = HeroTemplates.COLUMN_HERO_ID + " = ?";
            String[] selectionArgs = {heroStringId};

            rowsAffected = db.update(
                    HeroTemplates.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
            );

            if (rowsAffected > 0) {
                Log.d(TAG, "HeroTemplate actualizado: " + heroTemplate.getName() + " (StringID: " + heroStringId + ")");
            } else {
                Log.w(TAG, "Actualización de HeroTemplate falló o no afectó filas para StringID: " + heroStringId);
            }

        } catch (SQLiteException e) {
            Log.e(TAG, "Error actualizando HeroTemplate " + heroTemplate.getName() + ": " + e.getMessage(), e);
        } finally {
            // Singleton: no cerrar db aquí
        }
        return rowsAffected;
    }

    // ========== DELETE ==========

    public int deleteHeroTemplate(String heroStringId) {
        SQLiteDatabase db = null;
        int rowsDeleted = 0;

        if (heroStringId == null || heroStringId.isEmpty()) {
            Log.e(TAG, "Hero String ID es requerido para eliminar un HeroTemplate.");
            return 0;
        }

        try {
            db = dbHelper.getWritableDatabase();

            String selection = HeroTemplates.COLUMN_HERO_ID + " = ?";
            String[] selectionArgs = {heroStringId};

            rowsDeleted = db.delete(
                    HeroTemplates.TABLE_NAME,
                    selection,
                    selectionArgs
            );

            if (rowsDeleted > 0) {
                Log.d(TAG, "HeroTemplate eliminado. StringID: " + heroStringId);
            } else {
                Log.w(TAG, "Eliminación de HeroTemplate falló o no afectó filas para StringID: " + heroStringId);
            }

        } catch (SQLiteException e) {
            // Una causa común de fallo aquí sería una violación de Foreign Key si PlayerHeroes
            // todavía referencian este template y la FK está activa (PRAGMA foreign_keys=ON).
            Log.e(TAG, "Error eliminando HeroTemplate con StringID " + heroStringId + ": " + e.getMessage(), e);
        } finally {
            // Singleton: no cerrar db aquí
        }
        return rowsDeleted;
    }

    public int deleteAllHeroTemplates() {
        SQLiteDatabase db = null;
        int rowsDeleted = 0;
        try {
            db = dbHelper.getWritableDatabase();
            // Deberías considerar el impacto en PlayerHeroes antes de hacer esto.
            // Quizás eliminar primero todos los PlayerHeroes o tener ON DELETE CASCADE en la FK.
            rowsDeleted = db.delete(HeroTemplates.TABLE_NAME, null, null);
            Log.w(TAG, "TODOS los HeroTemplates eliminados. Filas afectadas: " + rowsDeleted + ". ¡Esto puede haber afectado a PlayerHeroes!");
        } catch (SQLiteException e) {
            Log.e(TAG, "Error eliminando todos los HeroTemplates: " + e.getMessage(), e);
        } finally {
            // Singleton: no cerrar db aquí
        }
        return rowsDeleted;
    }
}
