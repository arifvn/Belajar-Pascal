package com.lingkarkode.belajarpascal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lingkarkode.belajarpascal.QuizContract.*;

import java.util.ArrayList;

public class QuizDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "QuizPascal.db";
    private static final int DATABASE_VERSION = 1;

    //Buat instance agar hanya ada satu database untuk semua activity
    private static QuizDbHelper instance;

    private SQLiteDatabase db;

    private QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized QuizDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new QuizDbHelper(context.getApplicationContext());
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Ketika pertama kali kelas ini QuizDbHelper di Instansiasi -> Buat tabel baru
        //Tabel hanya sekali dibuat saat pertama install app
        //Kalau mau refactor column harus reinstall app atau panggil method onUpgrade dibawah
        this.db = db;

        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " +
                CategoriesTable.TABLE_NAME + "( " +
                CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoriesTable.COLUMN_NAME + " TEXT" +
                ")";

        final String SQL_CREATE_QUESTION_TABLE = "CREATE TABLE " +
                QuestionTable.TABLE_NAME + " ( " +
                QuestionTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionTable.COLUMN_QUESTION + " TEXT, " +
                QuestionTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionTable.COLUMN_OPTION4 + " TEXT, " +
                QuestionTable.COLUMN_ANSWER_NUMBER + " INTEGER, " +
                QuestionTable.COLUMN_CATEGORY_ID + " INTEGER, " +
                "FOREIGN KEY(" + QuestionTable.COLUMN_CATEGORY_ID + ") REFERENCES " +
                CategoriesTable.TABLE_NAME + "(" + CategoriesTable._ID + ")" + "ON DELETE CASCADE" +
                ")";

        //Eksekusi Perintah SQL pembuatan tabel diatas
        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_QUESTION_TABLE);

        //Isi tabel dengan data
        fillCategoriesTable();
        fillQuestionTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoriesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void fillCategoriesTable() {
        Category c1 = new Category("Latihan Soal 1");
        addCategory(c1);

        Category c2 = new Category("Latihan Soal 2");
        addCategory(c2);

        Category c3 = new Category("Latihan Soal 3");
        addCategory(c3);
    }

    private void addCategory(Category category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CategoriesTable.COLUMN_NAME, category.getName());
        db.insert(CategoriesTable.TABLE_NAME, null, contentValues);
    }

    private void fillQuestionTable() {
        //Isi object Question dengan data
        Question q1 = new Question("Latihan 1 : Tipe data bahasa pascal untuk TRUE FALSE adalah...",
                "A. String", "B. Char", "C.Boolean", "D. Byte", 3,
                Category.LATIHAN_SOAL_1);
        //Buat pasangan KUNCI dan ISI dari data tersebut -> Insert ke Tabel
        addQuestion(q1);

        Question q2 = new Question("Latihan 2 : Siapkah penemu program pascal...",
                "A. Greyson change", "B. Kondrazuse", "C. Prof. Niklaus Smirth", "D. Dr. Harcules", 3,
                Category.LATIHAN_SOAL_2);
        addQuestion(q2);
        Question q3 = new Question("Latihan 3 : Struktur bahasa pemograman pascal paling pertama adalah...",
                "A. User crt", "B. Var", "C. Begin", "D. WriteIn", 1,
                Category.LATIHAN_SOAL_3);
        addQuestion(q3);
        Question q4 = new Question("Latihan 2 : Tipe bilangan bulat dalam bahasa pascal dikenal sebagi ...",
                "A. Byte", "B. Integer", "C. Char", "D. String", 2,
                Category.LATIHAN_SOAL_2);
        addQuestion(q4);
        Question q5 = new Question("Latihan 1 : Prosedur yang digunakan untuk membersihkan layar saat program dijalankan adalah...",
                "A. Clrscr", "B. Begin", "C. Readln", "D. Writeln", 4,
                Category.LATIHAN_SOAL_1);
        addQuestion(q5);
    }

    private void addQuestion(Question question) {
        ContentValues contentValues = new ContentValues();
        //Buat pasangan KUNCI dan ISI dari data tersebut -> Insert ke Tabel
        contentValues.put(QuestionTable.COLUMN_QUESTION, question.getQuestion());
        contentValues.put(QuestionTable.COLUMN_OPTION1, question.getOption1());
        contentValues.put(QuestionTable.COLUMN_OPTION2, question.getOption2());
        contentValues.put(QuestionTable.COLUMN_OPTION3, question.getOption3());
        contentValues.put(QuestionTable.COLUMN_OPTION4, question.getOption4());
        contentValues.put(QuestionTable.COLUMN_ANSWER_NUMBER, question.getAnswerNumber());
        contentValues.put(QuestionTable.COLUMN_CATEGORY_ID, question.getCategoryId());

        db.insert(QuestionTable.TABLE_NAME, null, contentValues);
    }

    //Ambil kembali data(row) yang tersimpan dalam tabel, Masukan kembali ke object Category
    //Akumulasikan masing-masing object Category dalam bentuk List Category untuk mendapat semua row
    public ArrayList<Category> getAllCategory() {
        ArrayList<Category> categoryArrayList = new ArrayList<>();
        db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + CategoriesTable.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(CategoriesTable._ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(CategoriesTable.COLUMN_NAME)));
                categoryArrayList.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return categoryArrayList;
    }

    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + QuestionTable.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(cursor.getInt(cursor.getColumnIndex(QuestionTable._ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndex(QuestionTable.COLUMN_QUESTION)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(QuestionTable.COLUMN_OPTION1)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(QuestionTable.COLUMN_OPTION2)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(QuestionTable.COLUMN_OPTION3)));
                question.setOption4(cursor.getString(cursor.getColumnIndex(QuestionTable.COLUMN_OPTION4)));
                question.setAnswerNumber(cursor.getInt(cursor.getColumnIndex(QuestionTable.COLUMN_ANSWER_NUMBER)));
                question.setCategoryId(cursor.getInt(cursor.getColumnIndex(QuestionTable.COLUMN_CATEGORY_ID)));

                questionList.add(question);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return questionList;
    }

    public ArrayList<Question> getQuestions(int categoryId) {
        ArrayList<Question> questionArrayList = new ArrayList<>();
        db = getReadableDatabase();

        String selection = QuestionTable.COLUMN_CATEGORY_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(categoryId)};

        Cursor cursor = db.query(
                QuestionTable.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(cursor.getInt(cursor.getColumnIndex(QuestionTable._ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndex(QuestionTable.COLUMN_QUESTION)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(QuestionTable.COLUMN_OPTION1)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(QuestionTable.COLUMN_OPTION2)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(QuestionTable.COLUMN_OPTION3)));
                question.setOption4(cursor.getString(cursor.getColumnIndex(QuestionTable.COLUMN_OPTION4)));
                question.setAnswerNumber(cursor.getInt(cursor.getColumnIndex(QuestionTable.COLUMN_ANSWER_NUMBER)));
                question.setCategoryId(cursor.getInt(cursor.getColumnIndex(QuestionTable.COLUMN_CATEGORY_ID)));

                questionArrayList.add(question);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return questionArrayList;
    }
}
