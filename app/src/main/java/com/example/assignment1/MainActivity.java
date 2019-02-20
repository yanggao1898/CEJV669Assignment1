package com.example.assignment1;

import android.graphics.Path;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

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

    private DisplayListener Current_Number;
    private DisplayListener Op_Preview;
    private DisplayListener History;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reset();

        History = new DisplayListener(R.id.tv_history);
        DivZeroErr = false;


        /*
        double t1 = 12345.67000d;
        double t2 = 5.000d;

        String ts1 = Double.toString(t1);
        String ts2 = Double.toString(t2);

        long i2 = (long) t2;

        if (i2 == t2) {
            ts2 = Long.toString(i2);
        }
        */
        // setButtonSize();
    }

    private void setButtonSize() {
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


    public void buttonPressed(View v) {
        int buttonId = v.getId();

        Button b = (Button) v;

        String bText = b.getText().toString();

        //String x = getString(R.string.)
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
        Current_Number = new DisplayListener(R.id.tv_curNumber);
        Op_Preview = new DisplayListener(R.id.tv_opPreview);
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
        Numbers[Pos] = -1 * Numbers[Pos];
        LastOp = false;
        Current_Number.setString(num_display_helper(Numbers[Pos]));
    }

    public void prs_btn_per(View v) {
        Numbers[Pos] = Numbers[Pos]/100;
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

    private void prs_op_helper(OP opEnum) {

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

            Op_Preview.append(" ");
            Op_Preview.append(Current_Number.toString());
            Op_Preview.append(" ");
            Op_Preview.append(getString(R.string.btn_eql));
            Op_Preview.append(" ");
            Current_Number.setString(num_display_helper(Numbers[0]));
            Op_Preview.append(Current_Number.toString());

            //History.append(Op_Preview.toString());
            //History.append("/n");

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
}
