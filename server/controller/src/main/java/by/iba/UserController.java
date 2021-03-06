package by.iba;

import by.iba.dto.req.user.UserAvatarReqDTO;
import by.iba.dto.req.user.UserCredentialsReqDTO;
import by.iba.dto.req.user.UserPersonalDataReqDTO;
import by.iba.dto.resp.user.RespStatusDTO;
import by.iba.dto.resp.user.UserResp;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping(path = "/api/v1/users")
@CrossOrigin(origins = "*")
public interface UserController {

    @GetMapping()
    ResponseEntity<UserResp> getMe();

    @GetMapping("/{id}")
    ResponseEntity<UserResp> getUserById(@PathVariable Long id);

    @PutMapping("/{id}")
    ResponseEntity<UserResp> update(@PathVariable Long id, @RequestBody @Valid UserPersonalDataReqDTO userPersonalDataReqDTO, BindingResult bindingResult);

    @PatchMapping("/{id}/avatar")
    ResponseEntity<RespStatusDTO> updateAvatar(@PathVariable Long id, @RequestBody @Valid UserAvatarReqDTO userAvatarReqDTO, BindingResult bindingResult);

    @PatchMapping("/{id}/credentials")
    ResponseEntity<RespStatusDTO> updatePassword(@PathVariable Long id, @RequestBody @Valid UserCredentialsReqDTO userCredentialsReqDTO, BindingResult bindingResult);

}
