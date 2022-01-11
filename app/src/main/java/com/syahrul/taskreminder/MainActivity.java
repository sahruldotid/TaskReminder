package com.syahrul.taskreminder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int id;
    public ListView mListView, checkListView;
    public static List<Item> items = new ArrayList<>();
    public static List<Item> tmp = new ArrayList<>();
    public static ArrayList<Category> cat = new ArrayList<>();
    public static String ANDROID_CHANNEL_ID = "com.syahrul.taskreminder";
    public static String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";


    TextView nb_tasks;
    public static boolean aff_done, aff_todo, aff_passed, aff_ondate;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listView);
        checkListView = (ListView) findViewById(R.id.checkCat);
        nb_tasks = (TextView) findViewById(R.id.nb_tasks);
        aff_done = true;
        aff_todo = true;
        aff_passed = true;
        aff_ondate = true;
        id = 0;
        CheckBox checkToDo = (CheckBox) findViewById(R.id.switch_todo);
        checkToDo.setChecked(true);
        checkToDo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    aff_todo = true;
                else
                    aff_todo = false;
                affListCorresponding();
            }
        });
        CheckBox checkDone = (CheckBox) findViewById(R.id.switch_done);
        checkDone.setChecked(true);
        checkDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    aff_done = true;
                else
                    aff_done = false;
                affListCorresponding();
            }
        });
        CheckBox checkPassed = (CheckBox) findViewById(R.id.switch_passed);
        checkPassed.setChecked(true);
        checkPassed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    aff_passed = true;
                else
                    aff_passed = false;
                affListCorresponding();
            }
        });
        CheckBox checkOnDate = (CheckBox) findViewById(R.id.switch_ondate);
        checkOnDate.setChecked(true);
        checkOnDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    aff_ondate = true;
                else
                    aff_ondate = false;
                affListCorresponding();
            }
        });
        getData();
        getCatData();
        if (cat.size() == 0)
            cat.add(new Category("none", Color.parseColor("#262D3B")));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intentMain = new Intent(MainActivity.this, EditItemActivity.class);
                Item item = (Item) mListView.getAdapter().getItem(position);
                String title = item.getTitle();
                String time = item.getTime();
                String txt = item.getText();
                String date = item.getDate();
                String categorie = item.getCategory();
                intentMain.putExtra("position", String.valueOf(position));
                intentMain.putExtra("title", title);
                intentMain.putExtra("txt", txt);
                intentMain.putExtra("date", date);
                intentMain.putExtra("time", time);
                intentMain.putExtra("categorie", categorie);
                startActivityForResult(intentMain, 1);
            }
        });

        ItemAdapter adapter = new ItemAdapter(MainActivity.this, items);
        CheckAdapter adapter1 = new CheckAdapter(MainActivity.this, cat);
        checkListView.setAdapter(adapter1);
        mListView.setAdapter(adapter);
        checkDate();
    }

    /**
     * Recup les données des tasks dans la db
     */
    public void getData() {
        List<Item> list = new ArrayList<>();
        Item tmp;
        SQLiteDatabase mydatabase = openOrCreateDatabase("todolist", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tasks(Titre VARCHAR, Date VARCHAR, Status VARCHAR, Txt VARCHAR, Cat VARCHAR);");
        Cursor resultSet = mydatabase.rawQuery("Select * from tasks", null);
        resultSet.moveToFirst();
        int count = 0;
        while (count < resultSet.getCount()) {
            String title = resultSet.getString(resultSet.getColumnIndex("Titre"));
            String date = resultSet.getString(resultSet.getColumnIndex("Date"));
            String status = resultSet.getString(resultSet.getColumnIndex("Status"));
            String txt = resultSet.getString(resultSet.getColumnIndex("Txt"));
            String cat = resultSet.getString(resultSet.getColumnIndex("Cat"));
            Date d = new Date();
            SimpleDateFormat newDateFormat = new SimpleDateFormat("EE d MMM yyyyHH:mm");
            try {
                d = newDateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tmp = new Item(title, txt, d);
            if (status.equals(Item.Status.DONE.toString()))
                tmp.setStatus(Item.Status.DONE);
            else
                tmp.setStatus(Item.Status.TODO);
            tmp.setCategory(cat);
            list.add(tmp);
            count++;
            resultSet.moveToNext();
        }
        items = list;
    }

    /**
     * Recup les données des catégories dans la db
     */
    public void getCatData() {
        ArrayList<Category> list = new ArrayList<>();
        Category tmp;
        SQLiteDatabase mydatabase = openOrCreateDatabase("todolist", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS cats(Name VARCHAR, Color VARCHAR);");
        Cursor resultSet = mydatabase.rawQuery("Select * from cats", null);
        resultSet.moveToFirst();
        int count = 0;
        while (count < resultSet.getCount()) {
            String name = resultSet.getString(resultSet.getColumnIndex("Name"));
            String color = resultSet.getString(resultSet.getColumnIndex("Color"));
            tmp = new Category(name, Integer.parseInt(color));
            list.add(tmp);
            count++;
            resultSet.moveToNext();
        }
        cat = list;
    }

    /**
     * Returns value to insert in db
     *
     * @return
     */
    public String addToDataBase(int i) {
        Item tmp = items.get(i);
        String query = "'";
        query += tmp.getTitle() + "','";
        query += tmp.getDate() + tmp.getTime() + "','";
        query += tmp.getStatus().toString() + "','";
        query += tmp.getText() + "','";
        query += tmp.getCategory() + "'";
        return query;
    }

    /**
     * Sauvegarde les tasks dans la db
     */
    public void saveData() {
        String query;
        SQLiteDatabase mydatabase = openOrCreateDatabase("todolist", MODE_PRIVATE, null);
        mydatabase.execSQL("DROP TABLE IF EXISTS tasks");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tasks(Titre VARCHAR, Date VARCHAR, Status VARCHAR, Txt VARCHAR, Cat VARCHAR);");

        for (int i = 0; i < items.size(); i++) {
            query = addToDataBase(i);
            mydatabase.execSQL("INSERT INTO tasks VALUES(" + query + ");");
        }
    }

    public void saveCategory() {
        String query;
        SQLiteDatabase mydatabase = openOrCreateDatabase("todolist", MODE_PRIVATE, null);
        mydatabase.execSQL("DROP TABLE IF EXISTS cats");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS cats(Name VARCHAR, Color VARCHAR);");
        for (int i = 0; i < cat.size(); i++) {
            query = "'" + cat.get(i).getName() + "','" + String.valueOf(cat.get(i).getColor()) + "'";
            mydatabase.execSQL("INSERT INTO cats VALUES(" + query + ");");
        }
    }


    public void settings(View V) {
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }


    public void add(View v) {
        Intent intentMain = new Intent(MainActivity.this, AddItemActivity.class);
        startActivityForResult(intentMain, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String title = data.getStringExtra("title");
                String txt = data.getStringExtra("txt");
                String date = data.getStringExtra("date");
                String delete = data.getStringExtra("delete");
                String category = data.getStringExtra("category");
                SimpleDateFormat newDateFormat = new SimpleDateFormat("EE d MMM yyyy k:m");
                Date d = null;
                try {
                    d = newDateFormat.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (data.getStringExtra("edit").equals("true")) {
                    int position = Integer.parseInt(data.getStringExtra("position"));
                    try {
                        modifyItem(position, title, txt, d, delete, category);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Item newItem = new Item(title, txt, d);
                    newItem.setCategory(category);
                    try {
                        addToList(newItem);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //here goes nothing
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                saveCategory();
                checkCategories();
                affListCorresponding();
                ((CheckAdapter) checkListView.getAdapter()).notifyDataSetChanged();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                saveCategory();
                checkCategories();
                affListCorresponding();
                ((CheckAdapter) checkListView.getAdapter()).notifyDataSetChanged();
            }
        }
    }


    public void checkDate() {
        int i = 0;
        Date d;

        d = new Date();
        nb_tasks.setText(String.valueOf(items.size()) + " Tasks");
        while (i < items.size()) {
            if (!(items.get(i).getRealDate().after(d))) {
                items.get(i).setPassed(true);
                items.get(i).setDateColor("#FF0000");
            } else {
                items.get(i).setPassed(false);
                items.get(i).setDateColor("#121212");
            }
            i++;
        }
    }


    public void addToList(Item item) throws ParseException {
        items.add(item);
        checkDate();
        saveData();
        Date f = new Date();
        int c = 0;
        int color = Color.BLUE;
        while (c < cat.size()) {
            if (item.getCategory().equals(cat.get(c).getName())) {
                color = cat.get(c).getColor();
            }
            c++;
        }
        int delay = (int) (item.getRealDate().getTime() - f.getTime());
        if (delay > 0)
            scheduleNotification(getNotification(item.getTitle(), item.getText(), color), delay);
        affListCorresponding();
    }

    /**
     * Modifie un item déja existant
     *
     * @param position
     * @param title
     * @param txt
     * @param d
     * @param delete
     * @param cate
     * @throws ParseException
     */
    public void modifyItem(int position, String title, String txt, Date d, String delete, String cate) throws ParseException {
        Item item = items.get(position);
        if (delete.equals("false")) {
            item.setTitle(title);
            item.setText(txt);
            item.setDueDate(d);
            item.setCategory(cate);
        } else
            items.remove(item);
        checkDate();
        saveData();
        Date f = new Date();
        int delay = (int) (d.getTime() - f.getTime());
        int color = Color.BLUE;
        int c = 0;
        while (c < cat.size()) {
            if (item.getCategory().equals(cat.get(c).getName())) {
                color = cat.get(c).getColor();
            }
            c++;
        }
        if (delay > 0)
            scheduleNotification(getNotification(title, txt, color), delay);
        affListCorresponding();
    }


    /**
     * Permet de savoir si l'item doit être affiché en fonction de sa catégorie
     *
     * @param item
     * @return
     */
    public boolean showCatForItem(Item item) {
        int i = 0;
        while (i < cat.size()) {
            if (cat.get(i).getName().equals(item.getCategory())) {
                return cat.get(i).getShow();
            }
            i++;
        }
        return false;
    }

    /**
     * Permet d'afficher les tasks en fonctions des restrictions de l'utilisateur (statut, date, catégories etc...)
     */
    public void affListCorresponding() {
        int nb_items = items.size();
        boolean t, p;
        int i = 0;
        tmp.clear();
        while (i < nb_items) {
            t = false;
            p = false;
            if (aff_done && items.get(i).getStatus() == Item.Status.DONE)
                t = true;
            if (aff_todo && items.get(i).getStatus() == Item.Status.TODO)
                t = true;
            if ((aff_passed && items.get(i).getPassed()))
                p = true;
            if ((aff_ondate && !items.get(i).getPassed()))
                p = true;
            if (t && p && showCatForItem(items.get(i)))
                tmp.add(items.get(i));
            i++;
        }
        ItemAdapter adapter = new ItemAdapter(MainActivity.this, tmp);
        mListView.setAdapter(adapter);
        if (tmp.size() > 1)
            ((TextView) findViewById(R.id.nb_tasks)).setText(String.valueOf(tmp.size()) + " Tasks");
        else
            ((TextView) findViewById(R.id.nb_tasks)).setText(String.valueOf(tmp.size()) + " Task");
        adapter.notifyDataSetChanged();
    }

    /**
     * Passe le status d'une task en to do
     *
     * @param v
     */
    public void todoClick(View v) {
        final int position = mListView.getPositionForView((View) v.getParent());
        SwipeLayout s = (SwipeLayout) mListView.getChildAt(position);
        Item a = items.get(position);
        a.setStatus(Item.Status.TODO);
        affListCorresponding();
        saveData();
        s.close(true);
    }

    /**
     * Passe une catégorie en visible ou invisible
     *
     * @param v
     */
    public void catCheck(View v) {
        final int position = checkListView.getPositionForView((View) v.getParent());
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked())
            cat.get(position).setShow(true);
        else
            cat.get(position).setShow(false);
        affListCorresponding();
    }

    /**
     * Change le status de la task à Done
     *
     * @param v
     */
    public void doneClick(View v) {
        final int position = mListView.getPositionForView((View) v.getParent());
        SwipeLayout s = (SwipeLayout) mListView.getChildAt(position);
        Item a = items.get(position);
        a.setStatus(Item.Status.DONE);
        s.close(true);
        ItemAdapter b = (ItemAdapter) mListView.getAdapter();
        affListCorresponding();
        b.notifyDataSetChanged();
        saveData();
    }

    /**
     * Permet de preparer une notification
     *
     * @param notification
     * @param delay
     */
    private void scheduleNotification(Notification notification, int delay) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    /**
     * Permet à l'utilisateur de recevoir des notifications concernant ses taches
     *
     * @param content le contenu de la notification
     * @return un builder
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private Notification getNotification(String Title, String content, int color) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(Title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_notification);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(color);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("taskreminder");
        }
        affListCorresponding();
        return builder.build();
    }
    /*
    private  void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "android channel";
            String description = "Channel for task reminder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("taskreminder", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    */

    /**
     * Renvoies l'arraylist contenant les catégries
     *
     * @return l'array list contenant la ou les catégories
     */
    public static ArrayList<Category> getCatA() {
        ArrayList<Category> tmp = new ArrayList<Category>();
        int i = 0;
        while (i < cat.size())
            tmp.add(cat.get(i++));
        return tmp;
    }

    /**
     * Ferme le drawer menu
     *
     * @param v
     */
    public void closeMenu(View v) {
        DrawerLayout d = ((DrawerLayout) findViewById(R.id.drawer_layout));
        d.closeDrawers();
    }

    public static ArrayList<Category> getCat() {
        return (getCatA());
    }

    /**
     * Verifie si une catégorie à été supprimé et update les tasks si c'est le cas.
     */

    public void checkCategories() {
        int i = 0;

        while (i < items.size()) {
            int c = 0;
            boolean found = false;
            while (c < cat.size()) {
                if (items.get(i).getCategory().equals(cat.get(c).getName()))
                    found = true;
                c++;
            }
            if (!found)
                items.get(i).setCategory("none");
            i++;
        }
        affListCorresponding();
    }

    /**
     * Permet d'ajouter une catégorie via un menu
     *
     * @param v
     */
    public void addCategorie(View v) {
        Intent intentMain = new Intent(MainActivity.this, AddCategoryActivity.class);
        startActivityForResult(intentMain, 2);
        checkCategories();
    }

    public void logout(View v) {
        FirebaseAuth firebaseAuth;
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    //Do anything here which needs to be done after signout is complete
                     startActivity(new Intent(MainActivity.this, AuthActivity.class));
                }
            }
        };

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseAuth.signOut();
    }



    public void showAbout(View v){
        final Dialog dialog = new Dialog(this);
        //Mengeset judul dialog
        dialog.setTitle("About");

        //Mengeset layout
        dialog.setContentView(R.layout.about_dialog);

        //Membuat agar dialog tidak hilang saat di click di area luar dialog
         dialog.setCanceledOnTouchOutside(true);

        //Membuat dialog agar berukuran responsive
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

//        Button cancelButton = (Button) dialog.findViewById(R.id.button_cancel);
//        Button saveButton = (Button) dialog.findViewById(R.id.button_save);
//
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Data saved", Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
//            }
//        });

//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });

        //Menampilkan custom dialog
        dialog.show();
    }

    public void openMap(View v){
        startActivity(new Intent(this, MapsActivity.class));
    }
}

