package application.controllers;

import application.services.DBService;
import application.utils.responses.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/service")
public class ServiceController {

    private DBService dbService;

    @Autowired
    public ServiceController(DBService dbService) {
        this.dbService = dbService;
    }

    @PostMapping(path = "/clear")
    public ResponseEntity clearDB() {
        dbService.clearDB();
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("Database cleanup completed successfully"));
    }

    @GetMapping(path = "/status")
    public ResponseEntity getDBInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(dbService.getDBInfo());
    }
}
