package com.cartechindia.controller;

import com.cartechindia.entity.User;
import com.cartechindia.entity.UserStatus;
import com.cartechindia.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/{userId}/document/read")
//    public ResponseEntity<?> readUserDocument(@PathVariable Long userId) {
//        try {
//            User user = userService.findById(userId);
//
//            if (user.getDocument() == null || user.getDocument().isBlank()) {
//                return ResponseEntity.status(404)
//                        .body("No KYC document uploaded for this user.");
//            }
//
//            String documentPath = Path.of("/opt/app", user.getDocument()).toString();
//            Path path = Path.of(documentPath);
//            Resource resource = new UrlResource(path.toUri());
//
//            if (!resource.exists() || !resource.isReadable()) {
//                return ResponseEntity.status(404)
//                        .body("Document not found or not accessible.");
//            }
//
//            String fileName = resource.getFilename();
//            String contentType = "application/octet-stream";
//
//            if (fileName.endsWith(".pdf")) contentType = "application/pdf";
//            else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) contentType = "image/jpeg";
//            else if (fileName.endsWith(".png")) contentType = "image/png";
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.parseMediaType(contentType))
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
//                    .body(resource);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500)
//                    .body("Failed to read KYC document: " + e.getMessage());
//        }
//    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/unapproved")
    public ResponseEntity<List<User>> getUnapprovedUsers() {
        List<User> users = userService.getUnapprovedUsers();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}/status")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam UserStatus status
    ) {
        userService.updateUserStatus(userId, status);
        return ResponseEntity.ok("User status updated to %s".formatted(status));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}/document/read")
    public ResponseEntity<?> readAndApproveUserDocument(
            @PathVariable Long userId,
            @RequestParam(required = false) String action) {

        try {
            Resource resource = userService.getUserDocumentForApproval(userId, action);

            String fileName = resource.getFilename();
            String contentType = "application/octet-stream";

            if (fileName.endsWith(".pdf")) contentType = "application/pdf";
            else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) contentType = "image/jpeg";
            else if (fileName.endsWith(".png")) contentType = "image/png";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);

        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Failed to read KYC document: %s".formatted(e.getMessage()));
        }
    }


}
