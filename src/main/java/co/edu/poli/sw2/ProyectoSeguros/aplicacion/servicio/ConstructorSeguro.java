package co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio;

import java.math.BigDecimal;

import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.DatosSeguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroValidacionException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVehiculo;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVida;

/**
 * Valida unos {@link DatosSeguro} y construye el seguro de dominio
 * correspondiente a su tipo. Lo comparten los servicios de crear y
 * modificar, de modo que ambos aplican exactamente las mismas reglas.
 *
 * @author Alejandra Cano 
 */
final class ConstructorSeguro {

    private ConstructorSeguro() {
    }

    /**
     * Valida los datos y crea el seguro.
     *
     * @param datos datos capturados
     * @return seguro valido (sin persistir)
     * @throws SeguroValidacionException si algun dato no cumple las reglas
     */
    static Seguro construir(DatosSeguro datos) {

        if (datos == null) {
            throw new SeguroValidacionException(
                    "Los datos del seguro son obligatorios.");
        }

        String tipo = datos.getTipo();

        if (tipo == null) {
            throw new SeguroValidacionException(
                    "Debe seleccionar el tipo de seguro.");
        }

        ValidadorSeguro.validarNumero(datos.getNumero());
        ValidadorSeguro.validarFecha(datos.getFechaExpedicion());

        BigDecimal valor = ValidadorSeguro.parsearValor(datos.getValorTexto());

        ValidadorSeguro.validarEstado(datos.getEstado());
        ValidadorSeguro.validarAsegurado(datos.getAsegurado());

        String numero = datos.getNumero().trim();

        switch (tipo) {

            case SeguroVida.TIPO:
                ValidadorSeguro.validarBeneficiario(datos.getNombreBeneficiario());
                ValidadorSeguro.validarParentesco(datos.getParentesco());

                return new SeguroVida(
                        numero,
                        datos.getFechaExpedicion(),
                        valor,
                        datos.getEstado(),
                        datos.getAsegurado(),
                        datos.getNombreBeneficiario().trim(),
                        datos.getParentesco().trim());

            case SeguroVehiculo.TIPO:
                ValidadorSeguro.validarMarca(datos.getMarca());

                return new SeguroVehiculo(
                        numero,
                        datos.getFechaExpedicion(),
                        valor,
                        datos.getEstado(),
                        datos.getAsegurado(),
                        datos.getMarca().trim());

            default:
                throw new SeguroValidacionException(
                        "Tipo de seguro no soportado: " + tipo + ".");
        }
    }
}
