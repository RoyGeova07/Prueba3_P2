/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Binarios;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author royum
 */
public class EmpleadoMain {

    public static void main(String[] args) {
      Scanner lea = new Scanner(System.in).useDelimiter("\n");
        EmpleadoManager manager = new EmpleadoManager();
        boolean salir = false;

        while (!salir) {
            try {
                System.out.println("\n**MENU**");
                System.out.println("1- Agregar Empleado");
                System.out.println("2- Listar Empleados");
                System.out.println("3- Agregar Venta a Empleado");
                System.out.println("4- Pagar al Empleado");
                System.out.println("5- Despedir al Empleado");
                System.out.println("6- Ver Informacion de un Empleado");
                System.out.println("7- Salir");
                System.out.print("Escoja una opcion: ");
                int opcion = lea.nextInt();
                lea.nextLine(); 

                if (opcion == 1) {
                    System.out.print("Ingrese el nombre del empleado: ");
                    String nombre = lea.nextLine();
                    System.out.print("Ingrese el salario del empleado: ");
                    double salario = lea.nextDouble();
                    manager.addEmployee(nombre, salario);
                    System.out.println("Empleado agregado con exito.");
                } else if (opcion == 2) {
                    manager.employeeList();
                } else if (opcion == 3) {
                    System.out.print("Ingrese el codigo del empleado: ");
                    int codigoVenta = lea.nextInt();
                    System.out.print("Ingrese el monto de la venta: ");
                    double montoVenta = lea.nextDouble();

                    try {
                        if (montoVenta <= 0) {
                            System.out.println("El monto de la venta debe ser positivo.");
                        } else {
                            manager.addSaleToEmployee(codigoVenta, montoVenta);
                        }
                    } catch (IOException e) {
                        System.out.println("Error al intentar agregar la venta: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Error inesperado al agregar la venta. Verifique los datos.");
                    }
                } else if (opcion == 4) {
                    System.out.print("Ingrese el codigo del empleado a pagar: ");
                    int codigoPago = lea.nextInt();

                    try {
                        manager.payEmployee(codigoPago);
                    } catch (IOException e) {
                        System.out.println("Error al intentar pagar al empleado: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Error inesperado al realizar el pago. Verifique los datos.");
                    }
                } else if (opcion == 5) {
                    System.out.print("Ingrese el codigo del empleado a despedir: ");
                    int codigoDespedir = lea.nextInt();
                    if (manager.fireEmployee(codigoDespedir)) {
                        System.out.println("Empleado despedido con exito.");
                    } else {
                        System.out.println("No se pudo despedir al empleado (puede que no este activo).");
                    }
                } else if (opcion == 6) {
                    System.out.print("Ingrese el codigo del empleado para ver su informacion: ");
                    int codigoInfo = lea.nextInt();
                    manager.printEmployee(codigoInfo);
                } else if (opcion == 7) {
                    System.out.println("Saliendo del programa.");
                    salir = true;
                } else {
                    System.out.println("Opcion invalida, intente nuevamente.");
                }
            } catch (IOException e) {
                System.out.println("Ocurrio un error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Entrada invalida.");
                lea.nextLine(); 
            }
        }
    }
}