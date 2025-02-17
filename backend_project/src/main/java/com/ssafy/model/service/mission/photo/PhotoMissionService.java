package com.ssafy.model.service.mission.photo;

import com.ssafy.controller.mission.photo.PhotoUploadResponse;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.model.entity.Picture;
import com.ssafy.model.mapper.mission.photo.PictureMapper;
import com.ssafy.model.service.amazons3.S3Service;
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
    private final S3Service s3Service;

    public PhotoUploadResponse uploadPhoto(Long roomId, Long missionId, int groupNo, MultipartFile photo, Long userId) throws IOException {
        // S3에 파일 업로드
        String s3Directory = String.format("missions/%dRoom_%dGroupNo_%dMission", roomId, groupNo, missionId);
        String photoUrl = s3Service.uploadFile(photo, s3Directory);
        if (photoUrl == null) {
            throw new IOException("파일 저장에 실패했습니다.");
        }

        // Picture 엔티티 생성
        Picture picture = Picture.builder().roomId(roomId).userId(userId).missionId(missionId)
                .pictureUrl(photoUrl).completionTime(LocalDateTime.now()).build();

        int rows = pictureMapper.insertPicture(picture);
        if (rows == 0) {
            throw  new DatabaseOperationException("사진 등록에 실패했습니다.");
        }

        String presignedUrl = s3Service.generatePresignedUrl(photoUrl);
        return PhotoUploadResponse.builder()
                .pictureId(picture.getPictureId())
                .roomId(picture.getRoomId())
                .userId(picture.getUserId())
                .missionId(picture.getMissionId())
                .pictureUrl(presignedUrl)
                .completionTime(picture.getCompletionTime().toString())
                .build();
    }
}
