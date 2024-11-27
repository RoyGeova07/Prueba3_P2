/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Binarios;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author royum
 */
public class EmpleadoManager {
    
    private RandomAccessFile rcods,remps;
    /*
    el archivo binario necesita extension propia
    formato Codigo.mp
    int code 
    Formato Empleado.emp
    int code 
    String name 
    double salary
    long fecha Contratacion
    long fecha despido
    
    */
    
    public  EmpleadoManager(){
        
        try{
            
            //1-Asegurar que el folder se cree con el siguiente nombre
            File mf=new File("company");
            mf.mkdir();
            //2- Instanciar los archivos binarios de la carpeta principal
            
            rcods= new RandomAccessFile("company/codigos.emp","rw");//leer y escribir 
            remps=new RandomAccessFile("company/empleados.emp","rw");
            //3- inicializar el archivo de codigos si es nuevo.
            
            initcode();
        }catch(IOException e){
            
            System.out.println("ERROR");
            
        }
        
    }

    private void initcode() throws IOException{
       //un int pesa 4 bytes, no import el tamanio int = 4234324 = 4 bytes
        //cual es el primer codigo que se va a iniciliazar
        if(rcods.length()==0){//nos devolvera un long con el tamanio en bytes del archivo
            //puntero ->        0
            rcods.writeInt(1); 
            // puntero ->       4
        }
        
    }
    
    private int getCode() throws IOException{
        
        // puntero->`   0
        rcods.seek(0);// mueve manualmente el puntero hasta un byte especifico, 0 porque inicimos desde 0
        int code=rcods.readInt();
        //puntero->     4 
        rcods.seek(0);//no quiero que cargue mas 4 mas 4 por eso de nuevo en 0 
        rcods.writeInt(code+1);
        return code;
       
    }
    
    public void addEmployee(String name, double salary)throws IOException{
        //asegurar que el puntero es en el final del archivo.
        remps.seek(remps.length());//nos va a devolver el long,ahora va a empezar desde 36 y no en 0.
        remps.writeInt(0);
        //Puntero en -> 0
        int code=getCode();
        //Puntero en -> 4
        remps.writeUTF(name);//ana=8
        //Puntero en -> 12
        remps.writeDouble(salary);          //el timeinmillis no devuelve un long
        //Puntero en ->20
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        //Puntero en ->28
        remps.writeLong(0);//porque es una funcion que va agregar, no estoy contratandolo
        //Puntero en 36 bytes
        
        //asegurar crear folder y archivos individuales
            createEmployeeFolders(code);
    }
    
    private String employeeFolder(int code){
        //vas a integrar lo que es la carpeta, donde tendra subCarpetas para cada empleado
        return "company/emplado"+code;
        
    }
    
    private void createEmployeeFolders(int code)throws IOException{
        //crear folder empleado+code
        File edir=new File(employeeFolder(code));
        edir.mkdir();
        createYearSalesFileFor(code); 
    }
    
    //el usuario necestia crear archivo para las ventas que ha heccho, que vendio, en febrero,enero,marzo, hasta etc, con el anio
    //crear el archivo de venta
    private RandomAccessFile salesFileFor(int code)throws IOException{
        
        String dirPadre=employeeFolder(code);//get que es lo que quiero obtener
        int yearActual=Calendar.getInstance().get(Calendar.YEAR);//anio
        String path=dirPadre+"Ventas"+yearActual+".emp";//lo necesito para que el programa necesitara
        
        //por ultimo creamos el archivo, creamos un nuevo espacio en memoria, porque vamos 
        return new RandomAccessFile(path,"rw");
        
    }
    
    
    private void createYearSalesFileFor(int code)throws IOException{
        
        RandomAccessFile ryear=salesFileFor(code);
        if(ryear.length()==0){
            
            for (int mes = 0; mes < 12; mes++) {
                ryear.writeDouble(0);//cuanto a ganado en el mes con double  
                ryear.writeBoolean(true);//si se le pago o no al empleado, 9 espacios para cada mes
                
            }
            
        }
        
    }
    
    public void employeeList()throws IOException{
        
        remps.seek(0);//como esta en 0, lo tnemos que mover ese puntero, para que lea todos los registro, con un ciclo
        System.out.println("----Lista de empleados----");
        
        //Puntero->        0 < 36 True despues de la actulizacion 36 < 36 False
        while(remps.getFilePointer()<remps.length()){
            //Puntero -> 0
            int code=remps.readInt();
            //Puntero -> 4
            String nombre=remps.readUTF();//Ana P-> 8
            //Puntero -> 12
            double salario=remps.readDouble();
            //Puntero -> 20
            Date dateH=new Date(remps.readLong());
            //Puntero -> 28 //0 ==0 True
            if(remps.readLong()==0){
                
                System.out.println("Codigo: "+code+ " Nombre: "+nombre+ " Salario: Lps."+salario+ " Contratado el: "+dateH);
                
            }
            //Puntero ->36
            
        }
        
        
    }
    
    public boolean isEmployeeActive(int code)throws IOException{
        
        remps.seek(0);
        while(remps.getFilePointer()<remps.length()){
            //va a dejar el puntero justo despues del codigo
            int codigo=remps.readInt();
            long pos=remps.getFilePointer();
            remps.readUTF();
            remps.skipBytes(16);//no se actulizado ninguna fecha
            if(remps.readLong()==0&&codigo==code){
                remps.seek(pos);
                return true;
            }
            
        }
        return false;
        
    }
    
    public boolean fireEmployee(int code)throws IOException{
        
        if(isEmployeeActive(code)){
            
            String name=remps.readUTF();
            remps.skipBytes(16);//obtener fecha mas facil, contratacion
            remps.writeLong(new Date().getTime());
            System.out.println("Despidiendo a: "+name);
            return true;
            
        }
        return false;
        
    }
    
    public void addSaleToEmployee(int code, double monto) throws IOException {
        if (!isEmployeeActive(code)) {
            System.out.println("El empleado con codigo " + code + " no esta activo");
            return;
        }

        RandomAccessFile mesDe_Venta = salesFileFor(code);
        int Mes_Actual = Calendar.getInstance().get(Calendar.MONTH);
        long Posicion_Mes = Mes_Actual * 9;

        mesDe_Venta.seek(Posicion_Mes);
        double ventasActuales = mesDe_Venta.readDouble();

        mesDe_Venta.seek(Posicion_Mes);
        mesDe_Venta.writeDouble(ventasActuales + monto);

        boolean EstadoPago = mesDe_Venta.readBoolean();
        mesDe_Venta.seek(Posicion_Mes + 8);
        mesDe_Venta.writeBoolean(EstadoPago);

        System.out.println("Se ha agregado una venta de Lps. " + monto + " al empleado con codigo " + code + " para el mes " + (Mes_Actual + 1));
    }

    public void payEmployee(int code) throws IOException {
        if (!isEmployeeActive(code)) {
            System.out.println("ERROR: el empleado no esta activo");
            return;
        }

        RandomAccessFile Ventas_Archivo = salesFileFor(code);
        int mes = Calendar.getInstance().get(Calendar.MONTH);
        long posicionMes = mes * 9;

        Ventas_Archivo.seek(posicionMes);
        double ventas_del_mes = Ventas_Archivo.readDouble();
        boolean Ya_pagado = Ventas_Archivo.readBoolean();

        if (Ya_pagado) {
            System.out.println("El empleado con codigo " + code + " ya ha sido pagado este mes");
            return;
        }

        Ventas_Archivo.seek(posicionMes + 8);
        Ventas_Archivo.writeBoolean(true); // Marcar como pagado.

        remps.seek(0);

        while (remps.getFilePointer() < remps.length()) {
            int codigo = remps.readInt();
            if (codigo == code) {
                String nombre = remps.readUTF();
                double SalarioBase = remps.readDouble();
                long Fecha_de_pago = new Date().getTime();
                double comision = ventas_del_mes * 0.10;
                double sueldobase = SalarioBase + comision;
                double Deduccion = sueldobase * 0.035;
                double Sueldoneto = sueldobase - Deduccion;

                String RutaRecibo = employeeFolder(code) + "/recibos.emp";
                RandomAccessFile reciboArchivo = new RandomAccessFile(RutaRecibo, "rw");
                reciboArchivo.seek(reciboArchivo.length());

                reciboArchivo.writeLong(Fecha_de_pago);
                reciboArchivo.writeDouble(comision);
                reciboArchivo.writeDouble(sueldobase);
                reciboArchivo.writeDouble(Deduccion);
                reciboArchivo.writeDouble(Sueldoneto);
                reciboArchivo.writeInt(Calendar.getInstance().get(Calendar.YEAR));
                reciboArchivo.writeInt(mes + 1);

                System.out.println("Pago realizado a " + nombre + " con sueldo neto: Lps. " + Sueldoneto);
                return;
            } else {
                remps.skipBytes(28);
            }
        }
    }

    public void printEmployee(int code) throws IOException {
        remps.seek(0);

        boolean ENCONTRADO = false;

        while (remps.getFilePointer() < remps.length()) {
            int codigo = remps.readInt();

            if (codigo == code) {
                ENCONTRADO = true;
                String nombre = remps.readUTF();
                double salario = remps.readDouble();
                long fechaContratacion = remps.readLong();
                long fechaDespido = remps.readLong();

                System.out.println("Empleado:");
                System.out.println("Codigo: " + codigo);
                System.out.println("Nombre: " + nombre);
                System.out.println("Salario Lps. " + salario);
                System.out.println("Fecha de Contratacion: " + new Date(fechaContratacion));

                if (fechaDespido != 0) {
                    System.out.println("Fecha de despido: " + new Date(fechaDespido));
                } else {
                    System.out.println("Estado: Activo");
                }

                RandomAccessFile Ventas_Archivo = salesFileFor(code);
                double totalVentas = 0;

                System.out.println("Ventas anuales: ");
                for (int mes = 0; mes < 12; mes++) {
                    Ventas_Archivo.seek(mes * 9);
                    double ventasMes = Ventas_Archivo.readDouble();
                    totalVentas += ventasMes;

                    System.out.println("Mes " + (mes + 1) + " Lps. " + ventasMes);
                }

                System.out.println("Total de ventas anuales Lps. " + totalVentas);

                String rutaRecibos = employeeFolder(code) + "/recibos.emp";
                RandomAccessFile recibosArchivo = new RandomAccessFile(rutaRecibos, "r");
                int totalRecibos = (int) (recibosArchivo.length() / 40);
                System.out.println("Total de recibos historicos " + totalRecibos);

                return;
            } else {
                remps.skipBytes(28);
            }
        }

        if (!ENCONTRADO) {
            System.out.println("El empleado con codigo " + code + " no existe");
        }
    }
}