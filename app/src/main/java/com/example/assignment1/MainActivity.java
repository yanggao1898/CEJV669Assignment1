package com.example.assignment1;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private class DisplayListener {
        private StringBuilder Display_String;
        private int DispResID;

        private DisplayListener() {

        }

        private void UpdateDisplay() {
            ((TextView) findViewById(DispResID)).setText(Display_String.toString());
        }
        public DisplayListener(int drID) {
            Display_String = new StringBuilder();
            DispResID = drID;
        }

        public void append(String s) {
            Display_String.append(s);
            UpdateDisplay();
        }

        public void append(char s) {
            Display_String.append(s);
            UpdateDisplay();
        }

        public void setString(String s) {
            Display_String = new StringBuilder(s);
            UpdateDisplay();
        }

        public String toString() {
            return Display_String.toString();
        }

        public void reset() {
            Display_String = new StringBuilder();
            UpdateDisplay();
        }
        /*
        public void delete() {
            UpdateDisplay();
        }
        //*/
        public void setLength(int l) {
            Display_String.setLength(l);
            UpdateDisplay();
        }

        public int length() {
            return Display_String.length();
        }

        public int indexOf(String s) {
            return Display_String.indexOf(s);
        }
    }

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private CalcViewModel cvModel;

    private DisplayListener Current_Number;
    private DisplayListener Op_Preview;
    private StringBuilder History;
    // private DisplayListener History;

    private enum OP {
        NUL, ADD, SUB, MUL, DIV;
    }
    private OP CurOp;
    private boolean LastOp;
    private boolean LastNum;
    private boolean LastEql;
    private boolean DivZeroErr;

    private double[] Numbers;
    private byte Pos;

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        String v;
        if (itemId == R.id.nav_history) {
            Fragment test = getSupportFragmentManager().findFragmentByTag("HISTORY_FRAG");
            if (test == null || !test.isVisible()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment historyFrag = calc_history.newInstance(History.toString());
                ft.replace(R.id.main_container, historyFrag, "HISTORY_FRAG");
                ft.addToBackStack(null);
                ft.commit();
            }

        } else if (itemId == R.id.nav_about) {
            Fragment test = getSupportFragmentManager().findFragmentByTag("ABOUT_FRAG");
            if (test == null || !test.isVisible()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment aboutFrag = calc_about.newInstance();
                ft.replace(R.id.main_container, aboutFrag, "ABOUT_FRAG");
                ft.addToBackStack(null);
                ft.commit();
            }
        } else {
            v = "Something Weird Happened";
            Toast.makeText(this, v, Toast.LENGTH_LONG).show();
        }

        dl.closeDrawers();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment displayFrag = calc_display.newInstance();
        Fragment buttonsFrag = calc_buttons.newInstance();

        ft.add(R.id.main_container, displayFrag, "DISPFRAG");
        ft.add(R.id.main_container, buttonsFrag, "BTNFRAG");

        ft.commit();

        cvModel = ViewModelProviders.of(this).get(CalcViewModel.class);

        Current_Number = new DisplayListener(((calc_display) displayFrag).getCNID());
        Op_Preview = new DisplayListener(((calc_display) displayFrag).getOPID());
        History = new StringBuilder();
        // History = new DisplayListener(R.id.tv_history);
        reset();
        clear_zero_err();


        dl = findViewById(R.id.drawer_layout);
        t = new ActionBarDrawerToggle(this, dl,R.string.nav_open, R.string.nav_close);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView mNavView = findViewById(R.id.nav_view);
        mNavView.setNavigationItemSelectedListener(this);



        dl.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
            }

            @Override
            public void onDrawerStateChanged(int i) {
            }
        });



        // setButtonSize();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(t.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private void setButtonSize() {
        // UNUSED!
        // to set the button size to match
        Display disp =  getWindowManager().getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        int width = size.x;
        int height = size.y;

        LinearLayout txtContainer = findViewById(R.id.text_display_container);
        LinearLayout btnContainer = findViewById(R.id.button_layout_container);

        int textHeight = txtContainer.getMeasuredHeight();

        if (height - textHeight > width) {
            // most likely vertical orientation
            // width-bound the button sizes

            ViewGroup.LayoutParams p = btnContainer.getLayoutParams();
            p.height = btnContainer.getMeasuredWidth();
            btnContainer.setLayoutParams(p);

        } else {
            ViewGroup.LayoutParams p = btnContainer.getLayoutParams();
            p.width = btnContainer.getMeasuredHeight();
            btnContainer.setLayoutParams(p);
        }

    }

    private String num_display_helper(double in) {
        String returnIn = Double.toString(in);
        if (in <= Long.MAX_VALUE && in >= Long.MIN_VALUE) {
            long longIn = (long) in;
            if (longIn == in) {
                returnIn = Long.toString(longIn);
            }
        }
        return returnIn;
    }

    private void reset() {
        Current_Number.reset();
        Op_Preview.reset();
        CurOp = OP.NUL;
        LastOp = false;
        LastNum = false;
        LastEql = false;
        Numbers = new double[2];
        Arrays.fill(Numbers, 0);
        Pos = 0;
    }

    public void prs_btn_C(View v) {
        reset();
        clear_zero_err();
    }

    public void prs_btn_posneg(View v) {
        Double dispNum = Double.parseDouble(Current_Number.toString());
        Numbers[Pos] = -1 * dispNum;
        LastOp = false;
        Current_Number.setString(num_display_helper(Numbers[Pos]));
    }

    public void prs_btn_per(View v) {
        Double dispNum = Double.parseDouble(Current_Number.toString());
        Numbers[Pos] = dispNum/100;
        LastOp = false;
        Current_Number.setString(num_display_helper(Numbers[Pos]));
    }

    public void prs_btn_del(View v) {
        if(DivZeroErr) {
            clear_zero_err();
        }
        if (LastNum) {
            if(Current_Number.length() > 1) {
                Current_Number.setLength(Current_Number.length() - 1);
            } else if (Current_Number.length() == 1) {
                Current_Number.setString(getString(R.string.num_0));
            }
            Numbers[Pos] = Double.valueOf(Current_Number.toString());
        }
    }

    private void prs_num_helper(String p) {
        if(DivZeroErr) {
            clear_zero_err();
        }
        if(LastOp || LastEql) {

            if(p == getString(R.string.sym_dot)) {
                Current_Number.setString(getString(R.string.num_0));
                Current_Number.append(p);
            /*
            } else if (p == getString(R.string.num_0)) {
                Current_Number.setString(p);
            //*/
            } else {
                Current_Number.setString(p);
            }

        } else {
            if(p == getString(R.string.sym_dot)) {
                if(Current_Number.indexOf(getString(R.string.sym_dot)) == -1) {
                    Current_Number.append(p);
                }
            } else if (p.equals(getString(R.string.num_0))) {
                if(!Current_Number.toString().equals(getString(R.string.num_0))) {
                    Current_Number.append(p);
                }
            } else {
                if(Current_Number.toString().equals(getString(R.string.num_0))) {
                    Current_Number.setString(p);
                } else {
                    Current_Number.append(p);
                }

            }
        }


        LastOp = false;
        LastEql = false;
        LastNum = true;
        Numbers[Pos] = Double.valueOf(Current_Number.toString());
    }

    public void prs_btn_0(View v) {
        prs_num_helper(getString(R.string.num_0));
    }

    public void prs_btn_1(View v) {
        prs_num_helper(getString(R.string.num_1));
    }

    public void prs_btn_2(View v) {
        prs_num_helper(getString(R.string.num_2));
    }

    public void prs_btn_3(View v) {
        prs_num_helper(getString(R.string.num_3));
    }

    public void prs_btn_4(View v) {
        prs_num_helper(getString(R.string.num_4));
    }

    public void prs_btn_5(View v) {
        prs_num_helper(getString(R.string.num_5));
    }

    public void prs_btn_6(View v) {
        prs_num_helper(getString(R.string.num_6));
    }

    public void prs_btn_7(View v) {
        prs_num_helper(getString(R.string.num_7));
    }

    public void prs_btn_8(View v) {
        prs_num_helper(getString(R.string.num_8));
    }

    public void prs_btn_9(View v) {
        prs_num_helper(getString(R.string.num_9));
    }

    private void div_zero_err() {
        DivZeroErr = true;
        reset();
        Current_Number.setString(getString(R.string.err_div0));
        disable_buttons();
    }

    private void clear_zero_err() {
        DivZeroErr = false;
        Current_Number.setString(getString(R.string.num_0));
        enable_buttons();
    }

    private void disable_buttons() {
        findViewById(R.id.btn_posneg).setEnabled(false);
        findViewById(R.id.btn_per).setEnabled(false);
        findViewById(R.id.btn_opdiv).setEnabled(false);
        findViewById(R.id.btn_opmul).setEnabled(false);
        findViewById(R.id.btn_opsub).setEnabled(false);
        findViewById(R.id.btn_opadd).setEnabled(false);
        findViewById(R.id.btn_dot).setEnabled(false);
    }

    private void enable_buttons() {
        findViewById(R.id.btn_posneg).setEnabled(true);
        findViewById(R.id.btn_per).setEnabled(true);
        findViewById(R.id.btn_opdiv).setEnabled(true);
        findViewById(R.id.btn_opmul).setEnabled(true);
        findViewById(R.id.btn_opsub).setEnabled(true);
        findViewById(R.id.btn_opadd).setEnabled(true);
        findViewById(R.id.btn_dot).setEnabled(true);
    }

    private int get_op_from_enum(OP opEnum) {
        int opRes;

        switch (opEnum) {
            case ADD:
                opRes = R.string.btn_add;
                break;
            case SUB:
                opRes = R.string.btn_sub;
                break;
            case MUL:
                opRes = R.string.btn_mul;
                break;
            case DIV:
                opRes = R.string.btn_div;
                break;
            default:
                // maybe throw something
                opRes = 0;// SHOULD NEVER GET HERE
                break;
            //*/
        }

        return opRes;
    }

    private void prs_op_helper(OP opEnum) {

        int opRes = get_op_from_enum(opEnum);

        if(LastOp) {
            if(CurOp != opEnum) {
                Op_Preview.setLength(Op_Preview.length() - 1);
                Op_Preview.append(getString(opRes));
            }
        } else {
            if (Pos == 0) {
                Op_Preview.append(Current_Number.toString());
                Op_Preview.append(" ");
                Op_Preview.append(getString(opRes));
                Pos = 1;
            } else {

                if(CurOp != OP.NUL) {
                    switch (CurOp) {
                        case ADD:
                            Numbers[0] = Numbers[0] + Numbers[1];
                            break;
                        case SUB:
                            Numbers[0] = Numbers[0] - Numbers[1];
                            break;
                        case MUL:
                            Numbers[0] = Numbers[0] * Numbers[1];
                            break;
                        case DIV:
                            if (Numbers[1] != 0) {
                                Numbers[0] = Numbers[0] / Numbers[1];
                                break;
                            } else {
                                div_zero_err();
                                return;
                            }
                    }
                }

                Op_Preview.append(" ");
                Op_Preview.append(Current_Number.toString());
                Op_Preview.append(" ");
                Op_Preview.append(getString(opRes));
                Current_Number.setString(num_display_helper(Numbers[0]));
            }
        }
        CurOp = opEnum;
        LastOp = true;
        LastEql = false;
        LastNum = false;
    }

    public void prs_btn_div(View v) {
        prs_op_helper(OP.DIV);
    }

    public void prs_btn_mul(View v) {
        prs_op_helper(OP.MUL);
    }

    public void prs_btn_sub(View v) {
        prs_op_helper(OP.SUB);
    }

    public void prs_btn_add(View v) {
        prs_op_helper(OP.ADD);
    }

    public void prs_btn_eql(View v) {
        if (DivZeroErr) {
            clear_zero_err();
        } else {
            if (LastOp) {
                Numbers[Pos] = Double.parseDouble(Current_Number.toString());
            }

            switch (CurOp) {
                case ADD:
                    Numbers[0] = Numbers[0] + Numbers[1];
                    break;
                case SUB:
                    Numbers[0] = Numbers[0] - Numbers[1];
                    break;
                case MUL:
                    Numbers[0] = Numbers[0] * Numbers[1];
                    break;
                case DIV:
                    if (Numbers[1] != 0) {
                        Numbers[0] = Numbers[0] / Numbers[1];
                        break;
                    } else {
                        div_zero_err();
                        return;
                    }
            }

            if (LastEql) {
                Op_Preview.append(Current_Number.toString());
                Op_Preview.append(" ");
                Op_Preview.append(getString(get_op_from_enum(CurOp)));
                Op_Preview.append(" ");
                Op_Preview.append(num_display_helper(Numbers[1]));
            } else {
                Op_Preview.append(" ");
                Op_Preview.append(Current_Number.toString());
            }

            Op_Preview.append(" ");
            Op_Preview.append(getString(R.string.btn_eql));
            Op_Preview.append(" ");
            Current_Number.setString(num_display_helper(Numbers[0]));
            Op_Preview.append(Current_Number.toString());

            History.append(Op_Preview.toString());
            History.append("\n");
            Log.i("HISTORY-LOG: ", History.toString());

            Op_Preview.reset();

            Pos = 0;
            LastOp = false;
            LastNum = false;
            LastEql = true;

        }

    }

    public void prs_btn_dot(View v) {
        prs_num_helper(getString(R.string.sym_dot));
    }

    public void about_email(View v) {
        Intent eIntent = new Intent(Intent.ACTION_SENDTO);
        eIntent.setData(Uri.parse("mailto:"));
        eIntent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.email_address));
        eIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_desc));
        if (eIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(eIntent);
        }
    }

}
