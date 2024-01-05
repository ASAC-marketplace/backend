//package market.demo.controller;
//
//import lombok.RequiredArgsConstructor;
//import market.demo.service.S3Service;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/files")
//@RequiredArgsConstructor
//public class FileUploadController {
//
//    private final S3Service s3Service;
//
//    @PostMapping("/upload")
//    public ResponseEntity<?> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
//        try {
//            List<String> fileNames = new ArrayList<>();
//            for (MultipartFile file : files) {
//                String fileName = s3Service.uploadFile(file);
//                fileNames.add(fileName);
//            }
//            return ResponseEntity.ok().body(fileNames);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류가 발생했습니다.");
//        }
//    }
//}
