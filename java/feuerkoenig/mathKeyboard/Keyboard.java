package feuerkoenig.mathKeyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View;

import java.util.ArrayList;

import io.github.kexanie.library.MathView;

public class Keyboard extends LinearLayout implements View.OnClickListener{

    private MathView mv_formula;

    // Lists to store symbols left and right of the cursor
    private ArrayList<String> left = new ArrayList<>();
    private ArrayList<String> right = new ArrayList<>();

    // matches corresponding ID - Key Symbol pairs
    private SparseArray<String> keyValues = new SparseArray<>();

    // Grouping of symbols with similar insertion rules
    String [] sym_round_bracket = {"sin(", "cos(", "ln(", "\\sqrt(", "(", "log_{"};
    String [] sym_curly_bracket = {"\\sqrt{", "^{", "\\frac{"};

    String [] sym_closing = {")", "}", "}{", "}("};
    String [] sym_opening = {"sin(", "cos(", "ln(", "\\sqrt(", "(", "log_{", "\\sqrt{", "}{", "}(",
                            "^{", "\\frac{", ""};  // all symbols that open a new expression

    String [] sym_numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    String [] sym_arithmetic_wo_minus = {"+", "*", "^{"};
    String [] sym_signs = {"+", "-", "*"};

    public Keyboard(Context context, AttributeSet atrs){
        super(context, atrs, 0);
        initialize_keyboard(context);
    }

    public void set_view(MathView view_display){
        // The display view is used to show the formula
        mv_formula = view_display;
        // Show initial text ( \\, as white space in latex)
        mv_formula.setText("$$Enter\\,a\\,formula$$");
    }

    private void initialize_keyboard(Context context){
        // In-app keyboard is connected to values
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true);

        // IDs and corresponding key values in the order of appearance in the app
        int [] ids = {
                R.id.bt_plus, R.id.bt1, R.id.bt2, R.id.bt3, R.id.bt_klL, R.id.bt_klR, R.id.bt_check,
                R.id.bt_minus, R.id.bt4, R.id.bt5, R.id.bt6, R.id.bt_ln, R.id.bt_log, R.id.bt_del,
                R.id.bt_mult, R.id.bt7, R.id.bt8, R.id.bt9, R.id.bt_root, R.id.bt_pot, R.id.bt_sin,
                R.id.bt_frac, R.id.bt_dec, R.id.bt0, R.id.btX, R.id.bt_down, R.id.bte, R.id.bt_cos

        };
        String [] values = {
                "+", "1", "2", "3", "(", ")", "",
                "-", "4", "5", "6", "ln(", "log_{", "",
                "*", "7", "8", "9", "\\sqrt{", "^{", "sin(",
                "\\frac{", ".", "0", "x", "}", "e", "cos("
        };

        for(int i=0; i<ids.length; i++){
            initialize_button(ids[i], values[i]);
        }
    }

    private void initialize_button(int id, String value){
        // Makes individual button clickable and connected to its value
        Button btn = findViewById(id);
        btn.setOnClickListener(this);
        keyValues.put(id, value);
    }

    private void reset_text(){
        // Delete all written input
        left = new ArrayList<>();
        right = new ArrayList<>();
    }

    private void check(){
        // Fill in here what to do, if the check button is clicked
        // e.g. Calculate the result or reset the formula
        reset_text();
    }

    private void delete(){
        // Deletes the last symbol from the LaTex text if it is possible.
        if(left.size()==0){
            return;
        }

        String last_item = left.get(left.size()-1);
        left.remove(left.size() - 1);
        char last_char = last_item.charAt(last_item.length()-1);

        // closing brackets and brackets in logarithms or fractions are not deleted
        // instead the cursor is shifted to the left
        if(last_item.equals("}{") || last_item.equals("}(") || last_char=='}' || last_char == ')'){
            right.add(last_item);
        }
        // for fractions their two follow up symbols is deleted
        else if(last_item.equals("\\frac{") || last_item.equals("log_{")) {
            right.remove(right.size() - 1);
            right.remove(right.size() - 1);
        }
        // for opening brackets the closing bracket is deleted
        else if(last_char == '{' || last_char == '('){
            right.remove(right.size()-1);
        }
    }

    private boolean is_insertion_violations(String symbol) {
        // checks for input symbols incompatible with the current formula
        String last_symbol = "";
        String next_symbol = "";
        if(left.size()>0){
            last_symbol = left.get(left.size() - 1);
        }
        if(right.size()>0){
            next_symbol = right.get(right.size()-1);
        }
        if(is_contained(last_symbol, sym_opening) && is_contained(symbol, sym_arithmetic_wo_minus)){
            return true;  // no arithmetic sign after opening an expression. e.g. (*
        }
        if(!is_contained(last_symbol, sym_numbers) && symbol.equals(".")){
            return true;  // decimal point only after number
        }
        if(last_symbol.equals(".") && !is_contained(symbol, sym_numbers)){
            return true;  // only number after decimal point
        }
        if(is_contained(last_symbol, sym_signs) && is_contained(symbol, sym_signs)){
            return true;  // avoid double signs e.g. *+, ++, ...
        }
        if(is_contained(last_symbol, sym_signs) && symbol.equals("^{")){
            return true;  // no power after sign e.g. -^2
        }
        if(last_symbol.equals("}") && symbol.equals("^{")){
            return true;  // no power after closing expression: debatable!!!
        }
        if(is_contained(last_symbol, sym_opening) && is_contained(symbol, sym_closing)){
            return true;  // no brackets without content. e.g. ()
        }
        if(symbol.equals(")") && !next_symbol.equals(")")){
            return true;  // don't close wrong pair of brackets in nested functions
        }
        if(symbol.equals("}") && !is_contained(next_symbol, sym_closing)){
            return true;  // don't close wrong pair of brackets in nested functions
        }
        return false;
    }

    private void add_symbol(String symbol){
        // Add the pressed symbol and corresponding brackets if it is compliant with the rules.
        if(is_insertion_violations(symbol)){
            return;
        }
        if(symbol.equals("}")){
            // e.g. in fraction: symbol is not } but }{ to open the denominator
            left.add(right.get(right.size()-1));
        }
        else{
            left.add(symbol);
        }
        handle_brackets(symbol);
    }

    private void handle_brackets(String symbol){
        // add or remove corresponding closing brackets to the right for inserted opening brackets

        // round brackets
        if(is_contained(symbol, sym_round_bracket)){
            right.add(")");
            // add separator between basis and anti-log
            if(symbol.equals("log_{")) {
                right.add("}(");
            }
        }
        // curly brackets
        else if(is_contained(symbol, sym_curly_bracket)){
            right.add("}");
            // add separator between nominator and denominator
            if(symbol.equals("\\frac{")){
                right.add("}{");
            }
        }
        // shift delete closing bracket from the right side
        else if(symbol.equals(")") || symbol.equals("}")){
            right.remove(right.size() - 1);
        }
    }

    @Override
    public void onClick(View v) {
        // Starts action depending on which button is clicked.

        // Invoke action of check button
        if(v.getId()==R.id.bt_check){
            check();
        }
        // delete the last symbol
        else if(v.getId()==R.id.bt_del){
            delete();
        }
        // add new symbol
        else{
            add_symbol(keyValues.get(v.getId()));
        }
        // display the text, $$ used to indicate a latex format
        mv_formula.setText("$$" + get_latex_text() + "$$");
    }

    private String get_latex_text(){
        //  Return the typed text in latex format.
        StringBuilder text = new StringBuilder();

        // add symbols in front of the cursor
        for(int i = 0; i<left.size(); i++){
            text.append(left.get(i));
        }

        // add cursor symbol: Here, the ceiling symbol is used
        text.append("\\lceil");

        // add symbols after the cursor
        for(int i = right.size()-1; i >= 0; i--){
            text.append(right.get(i));
        }
        return text.toString();
    }

    private boolean is_contained(String value, String [] search_list){
        // Checks whether the value is a member of the list
        for(String cur_value : search_list){
            if(cur_value.equals(value)){
                return true;
            }
        }
        return false;
    }
}