package championoftaste.api.controller.competition;

import championoftaste.api.model.Nomination;
import championoftaste.api.model.NominationGroup;
import championoftaste.api.service.NominationGroupService;
import championoftaste.exception.ApiRequestException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/groups")
@AllArgsConstructor // создаёт конструктор с одним параметром для каждого поля в классе
public class NominationGroupController {

    private final NominationGroupService nominationGroupService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> create(@RequestBody NominationGroup nominationGroup) {
        try {
            nominationGroupService.create(nominationGroup);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Группа номинаций успешно создана", HttpStatus.CREATED);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_CONSUMER', 'ROLE_EXPERT', 'ROLE_ADMIN')")
    public ResponseEntity<List<NominationGroup>> read() {
        final List<NominationGroup> nominationGroups = nominationGroupService.readAll();

        return (nominationGroups != null && !nominationGroups.isEmpty())
                ? new ResponseEntity<>(nominationGroups, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{groupId}")
    @PreAuthorize("hasAnyRole('ROLE_CONSUMER', 'ROLE_EXPERT', 'ROLE_ADMIN')")
    public ResponseEntity<NominationGroup> read(@PathVariable Integer groupId) {
        final NominationGroup nominationGroup = nominationGroupService.read(groupId);

        return nominationGroup != null
                ? new ResponseEntity<>(nominationGroup, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{groupId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> update(@PathVariable Integer groupId, @RequestBody NominationGroup nominationGroup) {
        try {
            return nominationGroupService.update(groupId, nominationGroup)
                    ? new ResponseEntity<>("Группа номинаций успешно обновлена", HttpStatus.OK)
                    : new ResponseEntity<>("Не удалось найти группу номинаций", HttpStatus.NOT_FOUND);
        } catch (ApiRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Integer groupId) {
        return nominationGroupService.delete(groupId)
                ? new ResponseEntity<>("Группа номинаций успешно удалена", HttpStatus.OK)
                : new ResponseEntity<>("Не удалось найти группу номинаций", HttpStatus.NOT_FOUND);
    }

    @GetMapping("{groupId}/nominations")
    @PreAuthorize("hasAnyRole('ROLE_CONSUMER', 'ROLE_EXPERT', 'ROLE_ADMIN')")
    public ResponseEntity<List<Nomination>> readNominations(@PathVariable Integer groupId) {
        final NominationGroup nominationGroup = nominationGroupService.read(groupId);

        if (nominationGroup == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<Nomination> nominations = nominationGroup.getNominations();

        return (nominations != null && !nominations.isEmpty())
                ? new ResponseEntity<>(nominations, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
