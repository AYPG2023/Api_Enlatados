package com.ap.enlatados.service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import com.ap.enlatados.model.Usuario;
import com.ap.enlatados.model.Cliente;
import com.ap.enlatados.model.Repartidor;
import com.ap.enlatados.model.Vehiculo;

import com.ap.enlatados.repository.UsuarioRepository;
import com.ap.enlatados.repository.ClienteRepository;
import com.ap.enlatados.repository.RepartidorRepository;
import com.ap.enlatados.repository.VehiculoRepository;

@Service
public class CsvLoaderService {

    private final UsuarioRepository usuarioRepo;
    private final ClienteRepository clienteRepo;
    private final RepartidorRepository repartidorRepo;
    private final VehiculoRepository vehiculoRepo;

    public CsvLoaderService(UsuarioRepository usuarioRepo,
                            ClienteRepository clienteRepo,
                            RepartidorRepository repartidorRepo,
                            VehiculoRepository vehiculoRepo) {
        this.usuarioRepo    = usuarioRepo;
        this.clienteRepo    = clienteRepo;
        this.repartidorRepo = repartidorRepo;
        this.vehiculoRepo   = vehiculoRepo;
    }

    public void cargarUsuarios(Path csvPath) throws IOException {
        try (Reader r = Files.newBufferedReader(csvPath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withDelimiter(';')
                .withFirstRecordAsHeader()
                .parse(r);

            for (CSVRecord rec : records) {
                // columnas: ID;Nombre;Apellido;Contraseña
                Usuario u = new Usuario();
                // el ID se autogenera en DB
                u.setNombre(rec.get("Nombre"));
                u.setApellidos(rec.get("Apellido"));
                u.setEmail(rec.get("Correo")); 
                u.setPassword(rec.get("Contraseña"));
                usuarioRepo.save(u);
            }
        }
    }

    public void cargarClientes(Path csvPath) throws IOException {
        try (Reader r = Files.newBufferedReader(csvPath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withDelimiter(';')
                .withFirstRecordAsHeader()
                .parse(r);

            for (CSVRecord rec : records) {
                // columnas: DPI;Nombre;Apellido;Teléfono
                Cliente c = new Cliente();
                c.setDpi(rec.get("DPI"));
                c.setNombre(rec.get("Nombre"));
                c.setApellidos(rec.get("Apellido"));
                c.setTelefono(rec.get("Teléfono"));
                // dirección no viene en CSV; podrías asignar un default o leer otro campo
                c.setDireccion("");
                clienteRepo.save(c);
            }
        }
    }

    public void cargarRepartidores(Path csvPath) throws IOException {
        try (Reader r = Files.newBufferedReader(csvPath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withDelimiter(';')
                .withFirstRecordAsHeader()
                .parse(r);

            for (CSVRecord rec : records) {
                // columnas: DPI;Nombre;Apellido;Licencia;Teléfono
                Repartidor rp = new Repartidor();
                rp.setDpi(rec.get("DPI"));
                rp.setNombre(rec.get("Nombre"));
                rp.setApellidos(rec.get("Apellido"));
                rp.setLicencia(rec.get("Licencia"));
                rp.setTelefono(rec.get("Teléfono"));
                repartidorRepo.save(rp);
            }
        }
    }

    public void cargarVehiculos(Path csvPath) throws IOException {
        try (Reader r = Files.newBufferedReader(csvPath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withDelimiter(';')
                .withFirstRecordAsHeader()
                .parse(r);

            for (CSVRecord rec : records) {
                // columnas: Placa;Marca;Modelo;Color;Año;Tipo de transmisión
                Vehiculo v = new Vehiculo();
                v.setPlaca(rec.get("Placa"));
                v.setMarca(rec.get("Marca"));
                v.setModelo(rec.get("Modelo"));
                v.setColor(rec.get("Color"));
                v.setAnio(Integer.parseInt(rec.get("Año")));
                v.setTransmision(rec.get("Tipo de transmisión"));
                vehiculoRepo.save(v);
            }
        }
    }
}
