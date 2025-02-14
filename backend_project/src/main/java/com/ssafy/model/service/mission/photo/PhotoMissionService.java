package com.ssafy.model.service.mission.photo;

import com.ssafy.controller.mission.photo.PhotoUploadResponse;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.model.entity.Picture;
import com.ssafy.model.mapper.mission.photo.PictureMapper;
import com.ssafy.model.service.upload.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoMissionService {

    private final PictureMapper pictureMapper;
    private final UploadService uploadService;

    // 파일 저장 경로
//    private final String IMAGE_SAVE_DIR = System.getProperty("user.dir") + "/src/main/resources/static/images/pictures";
//
//    public PhotoUploadResponse uploadPhoto(Long roomId, Long missionId, int groupNo, MultipartFile photo, Long userId) {
//
//        String targetDirPath = IMAGE_SAVE_DIR + "/" + roomId + "_" + groupNo;
//        File dir = new File(targetDirPath);
//        if (!dir.exists()) {
//            boolean created = dir.mkdirs();
//            if (!created) {
//                throw new DatabaseOperationException("이미지 저장 디렉터리 생성 실패");
//            }
//        }
//
//        // 고유 파일명 생성
//        String originalFilename = photo.getOriginalFilename();
//        String fileExtension = "";
//        if (originalFilename != null && originalFilename.contains(".")) {
//            fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
//        }
//
//        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
//        File destination = new File(dir, uniqueFilename);
//
//        log.info("Saving file to: {}", destination.getAbsolutePath());
//
//        try {
//            // 파일 저장
//            photo.transferTo(destination);
//        } catch (IOException e) {
//            throw new DatabaseOperationException("파일 저장 중 오류 발생: " + e.getMessage());
//        }
//
//        // 상대 경로 생성
//        String relativePath = "images/pictures/" + roomId + "_" + groupNo + "/" + uniqueFilename;
//
//        // Picture 엔티티 생성 및 DB 저장
//        Picture picture = Picture.builder()
//                .roomId(roomId)
//                .userId(userId)
//                .missionId(missionId)
//                .pictureUrl(relativePath)
//                .completionTime(LocalDateTime.now())
//                .build();
//
//        int rows = pictureMapper.insertPicture(picture);
//        if (rows == 0) {
//            throw new DatabaseOperationException("사진 등록에 실패했습니다.");
//        }
//
//        return PhotoUploadResponse.builder()
//                .pictureId(picture.getPictureId())
//                .roomId(picture.getRoomId())
//                .userId(picture.getUserId())
//                .missionId(picture.getMissionId())
//                .pictureUrl(picture.getPictureUrl())
//                .completionTime(picture.getCompletionTime().toString())
//                .build();
//    }


}
