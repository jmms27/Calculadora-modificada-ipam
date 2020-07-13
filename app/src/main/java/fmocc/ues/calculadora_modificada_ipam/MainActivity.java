package fmocc.ues.calculadora_modificada_ipam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtResultado;
    private TextView txtHistorico;
    private String operador;
    private String operador1;
    private String operador2;
    private int lastBnt;
    private boolean clearEquals = false;
    private boolean enable = true;
    boolean Sipunto = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtResultado = findViewById(R.id.txtResultado);
        txtHistorico = findViewById(R.id.txtHistorico);

        operador = "";
        lastBnt = 0;
        for (int i = 0; i <= 9; i++) {
            int id = getResources().getIdentifier("btn" + i, "id", getPackageName());
            (findViewById(id)).setOnClickListener(this);
        }
        (findViewById(R.id.btnSuma)).setOnClickListener(this);
        (findViewById(R.id.btnResta)).setOnClickListener(this);
        (findViewById(R.id.btnMultiplicacion)).setOnClickListener(this);
        (findViewById(R.id.btnDivision)).setOnClickListener(this);
        (findViewById(R.id.btnC)).setOnClickListener(this);
        (findViewById(R.id.btnCE)).setOnClickListener(this);
        (findViewById(R.id.btnCuadrado)).setOnClickListener(this);
        (findViewById(R.id.btnRaiz)).setOnClickListener(this);
        (findViewById(R.id.btnIgual)).setOnClickListener(this);
        (findViewById(R.id.ibtnBorrar)).setOnClickListener(this);
        (findViewById(R.id.btnPunto)).setOnClickListener(this);
        (findViewById(R.id.btnPi)).setOnClickListener(this);


    }

    public void showMessage(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }


    public String removeLastCharacter(String str) {
        String result = "";
        if ((str != null) && (str.length() > 0)) {
            result = str.substring(0, str.length() - 1);
        }
        return result;
    }

    //que no exista un valor con punto decimal a medias (. NO)-(5. NO)-(5.5 SI)
    public boolean validInput(String cadena) {
        if (cadena.length() == 0) {
            return false;
        }

        if (cadena.indexOf('.') == cadena.length() - 1) {
            return false;
        }
        return true;
    }

    public String removeFromString(String str, int times) {
        String result = "";
        for (int i = 0; i < str.length() - times; i++) {
            result += str.charAt(i);
        }
        return result;
    }

    public boolean isButtonOperator(int lastButton) {
        int operatations[] = {R.id.btnSuma, R.id.btnResta, R.id.btnMultiplicacion, R.id.btnDivision, R.id.btnCuadrado};
        for (int i = 0; i < operatations.length; i++) {
            if (operatations[i] == lastButton) {
                return true;
            }
        }
        return false;
    }

    public boolean isButtonEspecial(int lastButton) {
        int operatations[] = {R.id.btnCuadrado, R.id.btnRaiz};
        for (int i = 0; i < operatations.length; i++) {
            if (operatations[i] == lastButton) {
                return true;
            }
        }
        return false;
    }

    public boolean isButtonNumber(int idBtn) {
        for (int i = 0; i <= 9; i++) {
            int id = getResources().getIdentifier("btn" + i, "id", getPackageName());
            if (id == idBtn) {
                return true;
            }
        }
        return false;
    }

    public void operation(String historico, int opciones, int lastBnt, String operador) {
        if (clearEquals) {
            historico = txtResultado.getText().toString();
            historico += operador;
            clearEquals = false;
        } else if (lastBnt != opciones) {            //Si el boton se repite no hacer nada

            //Si el anterior y el nuevo son botones operador solo sustituir la operacion
            if (isButtonOperator(lastBnt) && isButtonOperator(opciones) || isButtonEspecial(lastBnt)) {
                historico = removeFromString(historico, 3);
                if (!historico.isEmpty()) {
                    historico += operador;
                }
            } else if (validInput(txtResultado.getText().toString())) {
                historico += txtResultado.getText().toString();
                historico += operador;
            }

        }
        txtHistorico.setText(historico);
    }

    public void punto(View v) {//boton punto validado
        if (!Sipunto) {
            if (operador == "0") {
                operador1 = operador1 + "0.";
                Sipunto = true;
            } else {
                operador1 = operador1 + ".";
                Sipunto = true;
            }
            operador = "0";
        }
        txtHistorico.getText().toString();
        txtResultado.setText(operador);
    }


    public void operationEspecial(String historico, int opciones, int lastButton, String operador1, String operador2) {
        if (txtResultado.getText().toString().isEmpty()) {
            showMessage("Favor Ingrese algun valor numerico");
            opciones = 0;
            return;
        }
        historico = removeFromString(historico, 3);
        if (!txtHistorico.getText().toString().isEmpty()) {
            historico += " + ";
        }
        historico += operador1 + txtResultado.getText().toString() + operador2;
        historico += " + ";
        txtHistorico.setText(historico);
    }

    public void operationPi(String historico, int opciones, int lastButton) {
        historico = removeFromString(historico, 3);
        if (!txtHistorico.getText().toString().isEmpty()) {
            historico += " + ";
        }
        historico += txtResultado.getText().toString() + " 3.14159 ";
        txtHistorico.setText(historico);
    }

    public double solveString(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }


            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('X')) x *= parseFactor(); // multiplication
                    else if (eat('÷')) {
                        if (x == '0') {
                            showMessage("No se puede dividir entre 0");
                            Toast.makeText(getApplicationContext(), "No se puede dividir entre 0", Toast.LENGTH_SHORT).show();
                        } else {
                            x /= parseFactor(); // division
                        }
                    } else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }

    @Override
    public void onClick(View v) {
        int opciones = v.getId();

        if (opciones == R.id.btnBloqueo) {
            enable = !enable;
            if (enable) {
                Toast.makeText(getApplicationContext(), "Calculadora encendida", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Calculadora apagada", Toast.LENGTH_SHORT).show();
            }
        }

        if (enable) {
            //Necesita refrescar el campo de resultado needsToClearInput
            if (isButtonOperator(lastBnt) && isButtonNumber(opciones)) {
                txtResultado.setText("");
            }

            //Vaciar el Resultado si ha presionado el boton de IGUAL antes y el nuevo es un numero
            if (lastBnt == R.id.btnIgual && isButtonNumber(opciones)) {
                txtResultado.setText("");
            }

            String historico = txtHistorico.getText().toString();

            switch (opciones) {
                case R.id.btn0:
                    txtResultado.setText(txtResultado.getText() + "0");
                    break;
                case R.id.btn1:
                    txtResultado.setText(txtResultado.getText() + "1");
                    break;
                case R.id.btn2:
                    txtResultado.setText(txtResultado.getText() + "2");
                    break;
                case R.id.btn3:
                    txtResultado.setText(txtResultado.getText() + "3");
                    break;
                case R.id.btn4:
                    txtResultado.setText(txtResultado.getText() + "4");
                    break;
                case R.id.btn5:
                    txtResultado.setText(txtResultado.getText() + "5");
                    break;
                case R.id.btn6:
                    txtResultado.setText(txtResultado.getText() + "6");
                    break;
                case R.id.btn7:
                    txtResultado.setText(txtResultado.getText() + "7");
                    break;
                case R.id.btn8:
                    txtResultado.setText(txtResultado.getText() + "8");
                    break;
                case R.id.btn9:
                    txtResultado.setText(txtResultado.getText() + "9");
                    break;
                case R.id.btnPunto:
                    txtResultado.setText(txtResultado.getText() + ".");
                    break;
                case R.id.btnPi:
                    txtResultado.setText(txtResultado.getText() + "3.14159");
                    break;
                case R.id.btnRaiz:
                    operador1 = " sqrt(";
                    operador2 = ")";
                    operationEspecial(historico, opciones, lastBnt, operador1, operador2);
                    break;
                case R.id.btnCuadrado:
                    operador1 = " (";
                    operador2 = ")^2";
                    operationEspecial(historico, opciones, lastBnt, operador1, operador2);
                    break;

                case R.id.btnCE:
                    txtResultado.setText("");
                    txtHistorico.setText("");
                    break;
                case R.id.btnC:
                    txtResultado.setText("");
                    lastBnt = 0;
                    break;

                case R.id.ibtnBorrar:
                    txtResultado.setText(removeLastCharacter(txtResultado.getText().toString()));
                    break;
                case R.id.btnSuma:
                    operador = " + ";
                    operation(historico, opciones, lastBnt, operador);
                    break;
                case R.id.btnResta:
                    operador = " - ";
                    operation(historico, opciones, lastBnt, operador);
                    break;

                case R.id.btnMultiplicacion:
                    operador = " X ";
                    operation(historico, opciones, lastBnt, operador);
                    break;
                case R.id.btnDivision:
                    operador = " ÷ ";
                    operation(historico, opciones, lastBnt, operador);
                    break;
                case R.id.btnIgual:
                    operador = " = ";
                    String ecuacion = "";

                    //Evitar que este vacio
                    if (txtResultado.getText().toString().isEmpty()) {
                        opciones = 0;
                        showMessage("Favor Ingrese algun valor numerico");
                        break;
                    }

                    //Esto hace que se agrege el ultimo valor del campo resultado si lo amerita
                    if (clearEquals) {
                        if (isButtonEspecial(lastBnt)) {
                            historico = operador1 + txtResultado.getText().toString() + operador2;
                            historico += operador;
                        } else {
                            historico = txtResultado.getText().toString();
                            historico += operador;
                        }
                        clearEquals = false;
                    } else if (isButtonNumber(lastBnt)) {
                        historico += txtResultado.getText().toString();
                        historico += operador;
                    } else {
                        historico = removeFromString(historico, 3);
                        historico += operador;
                    }
                    txtHistorico.setText(historico);
                    ecuacion = removeFromString(historico, 3);
                    Double result = (Double) solveString(ecuacion);
                    ecuacion = String.format("%.4f", result);


                    if (ecuacion.equals("Infinity")) {
                        showMessage("El resultado tiende al Infinito");
                        txtResultado.setText("");
                        txtHistorico.setText("");
                    } else if (ecuacion.equals("NaN")) {
                        showMessage("El resultado es Indefinido");
                        txtResultado.setText("");
                        txtHistorico.setText("");
                    } else {
                        txtResultado.setText(ecuacion);
                    }

                    clearEquals = true;
                    break;
            }

            lastBnt = opciones;

        }
    }

}
