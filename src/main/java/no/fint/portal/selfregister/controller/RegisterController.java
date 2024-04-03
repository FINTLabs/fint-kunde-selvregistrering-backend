package no.fint.portal.selfregister.controller;


//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import no.fint.portal.exceptions.EntityFoundException;
import no.fint.portal.exceptions.EntityNotFoundException;
import no.fint.portal.exceptions.UpdateEntityMismatchException;
import no.fint.portal.model.ErrorResponse;
import no.fint.portal.model.contact.Contact;
import no.fint.portal.model.contact.ContactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.UnknownHostException;

@Slf4j
@RestController
//@Api(tags = "Self Register")
@RequestMapping(value = "/api/self/register")
public class RegisterController {

    private final ContactService contactService;

    public RegisterController(ContactService contactService) {
        this.contactService = contactService;
    }


//    @ApiOperation("Add new contact")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> addContact(
            @RequestHeader(name = "x-nin") String nin,
            @RequestBody final Contact contact) {
        if (!nin.equals(contact.getNin())) {
            throw new UpdateEntityMismatchException("Invalid NIN");
        }
        log.info("Contact: {}", contact);
        if (contactService.addContact(contact)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(contact);
        }

        throw new EntityFoundException(
                ServletUriComponentsBuilder
                        .fromCurrentRequest().path("/{nin}")
                        .buildAndExpand(contact.getNin()).toUri().toString()
        );
    }

//    @ApiOperation("Update contact")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> updateContact(
            @RequestHeader(name = "x-nin") String nin,
            @RequestBody final Contact contact) {
        if (!nin.equals(contact.getNin())) {
            throw new UpdateEntityMismatchException("Invalid NIN");
        }
        log.info("Contact: {}", contact);

        if (contactService.updateContact(contact)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(contact);
        }

        throw new RuntimeException(
                ServletUriComponentsBuilder
                        .fromCurrentRequest().path("/{nin}")
                        .buildAndExpand(nin).toUri().toString()
        );
    }

    @GetMapping
    public Contact getContact(@RequestHeader(name = "x-nin") String nin) {
        return contactService.getContact(nin).orElseThrow(() -> new EntityNotFoundException(nin));
    }

    @DeleteMapping
    public void deleteContact(@RequestHeader(name = "x-nin") String nin) {
        contactService.getContact(nin).ifPresent(contactService::deleteContact);
    }


    //
    // Exception handlers
    //
    @ExceptionHandler(UpdateEntityMismatchException.class)
    public ResponseEntity<ErrorResponse> handleUpdateEntityMismatch(Exception e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(EntityFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityFound(Exception e) {
        return ResponseEntity.status(HttpStatus.FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(NameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNameNotFound(Exception e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<ErrorResponse> handleUnkownHost(Exception e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorResponse(e.getMessage()));
    }
}
