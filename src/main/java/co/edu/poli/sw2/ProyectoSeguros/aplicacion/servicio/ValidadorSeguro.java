package co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.regex.Pattern;

import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroValidacionException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Asegurado;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.EstadoSeguro;

/**
 * Contiene las reglas de validacion utilizadas por
 * los casos de uso relacionados con seguros.
 *
 * @author Alejandra Cano 
 */
public final class ValidadorSeguro {

    private static final Pattern TIENE_ALFANUMERICO = Pattern.compile("[A-Za-z0-9]");

    private static final Pattern TIENE_LETRA = Pattern.compile("\\p{L}");

    private static final BigDecimal VALOR_MAXIMO = new BigDecimal("999999999999.99");

    private static final int MAX_NUMERO = 30;
    private static final int MAX_NOMBRE = 100;
    private static final int MAX_TEXTO = 50;

    /**
     * Evita crear instancias del validador.
     */
    private ValidadorSeguro() {
    }

    /**
     * Valida el numero del seguro.
     *
     * @param numero numero capturado
     */
    public static void validarNumero(String numero) {

        if (numero == null || numero.isBlank()) {
            throw new SeguroValidacionException(
                    "El numero del seguro es obligatorio.");
        }

        if (!TIENE_ALFANUMERICO.matcher(numero).find()) {
            throw new SeguroValidacionException(
                    "El numero del seguro debe contener caracteres alfanumericos.");
        }

        if (numero.trim().length() > MAX_NUMERO) {
            throw new SeguroValidacionException(
                    "El numero del seguro no puede superar " + MAX_NUMERO + " caracteres.");
        }
    }

    /**
     * Valida la fecha de expedicion.
     *
     * @param fecha fecha capturada
     */
    public static void validarFecha(LocalDate fecha) {

        if (fecha == null) {
            throw new SeguroValidacionException(
                    "La fecha de expedicion es obligatoria.");
        }

        if (fecha.isAfter(LocalDate.now())) {
            throw new SeguroValidacionException(
                    "La fecha de expedicion no puede ser futura.");
        }
    }

    /**
     * Convierte y valida el valor asegurado.
     *
     * @param valorTexto valor capturado (acepta coma o punto decimal)
     * @return valor con dos decimales
     */
    public static BigDecimal parsearValor(String valorTexto) {

        if (valorTexto == null || valorTexto.isBlank()) {
            throw new SeguroValidacionException(
                    "El valor es obligatorio.");
        }

        BigDecimal valor;

        try {
            valor = new BigDecimal(valorTexto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new SeguroValidacionException(
                    "El valor debe ser un numero valido.");
        }

        if (valor.signum() <= 0) {
            throw new SeguroValidacionException(
                    "El valor debe ser mayor que cero.");
        }

        valor = valor.setScale(2, RoundingMode.HALF_UP);

        if (valor.compareTo(VALOR_MAXIMO) > 0) {
            throw new SeguroValidacionException(
                    "El valor es demasiado grande.");
        }

        return valor;
    }

    /**
     * Valida el estado.
     *
     * @param estado estado seleccionado
     */
    public static void validarEstado(EstadoSeguro estado) {

        if (estado == null) {
            throw new SeguroValidacionException(
                    "Debe seleccionar el estado del seguro.");
        }
    }

    /**
     * Valida el asegurado.
     *
     * @param asegurado asegurado seleccionado
     */
    public static void validarAsegurado(Asegurado asegurado) {

        if (asegurado == null) {
            throw new SeguroValidacionException(
                    "Debe seleccionar un asegurado.");
        }
    }

    /**
     * Valida el nombre del beneficiario.
     *
     * @param nombre nombre capturado
     */
    public static void validarBeneficiario(String nombre) {

        validarTextoConLetras(nombre, "El nombre del beneficiario", MAX_NOMBRE);
    }

    /**
     * Valida el parentesco.
     *
     * @param parentesco parentesco capturado
     */
    public static void validarParentesco(String parentesco) {

        validarTextoConLetras(parentesco, "El parentesco", MAX_TEXTO);
    }

    /**
     * Valida la marca del vehiculo.
     *
     * @param marca marca capturada
     */
    public static void validarMarca(String marca) {

        if (marca == null || marca.isBlank()) {
            throw new SeguroValidacionException(
                    "La marca es obligatoria.");
        }

        if (!TIENE_ALFANUMERICO.matcher(marca).find()) {
            throw new SeguroValidacionException(
                    "La marca debe contener caracteres alfanumericos.");
        }

        if (marca.trim().length() > MAX_TEXTO) {
            throw new SeguroValidacionException(
                    "La marca no puede superar " + MAX_TEXTO + " caracteres.");
        }
    }

    private static void validarTextoConLetras(String texto, String campo, int maximo) {

        if (texto == null || texto.isBlank()) {
            throw new SeguroValidacionException(campo + " es obligatorio.");
        }

        if (!TIENE_LETRA.matcher(texto).find()) {
            throw new SeguroValidacionException(
                    campo + " debe contener letras.");
        }

        if (texto.trim().length() > maximo) {
            throw new SeguroValidacionException(
                    campo + " no puede superar " + maximo + " caracteres.");
        }
    }
}
