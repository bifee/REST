package br.edu.utfpr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/carro")
public class CarroController {
    @Autowired
    private CarroService carroService;

    @GetMapping
    public List<CarroComId> listAll(){
        return  carroService.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarroComId> read(@PathVariable("id") int id){
        CarroComId carro = carroService.read(id);
        if (carro == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(carro);
    }

    @PostMapping
    public void insert(@RequestBody Carro carro){
        carroService.insert(carro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id){
        if(carroService.delete(id)){
            return ResponseEntity.ok().build();
        } else return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") int id, @RequestBody Carro carro){
        if(carroService.update(id, carro)){
            return ResponseEntity.ok().build();
        } else return ResponseEntity.notFound().build();
    }
}
