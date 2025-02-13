package com.ssafy.model.service.problem;

import com.ssafy.controller.problem.ProblemRequest;
import com.ssafy.model.entity.HeritageProblem;
import com.ssafy.model.mapper.problem.ProblemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemMapper problemMapper;

    @Transactional
    public void createHeritageProblem(ProblemRequest.HeritageRequest dto, String problemImageS3Key, String objectImageS3Key) {
        HeritageProblem heritageProblemsDto = HeritageProblem.builder()
                .cardId(dto.getCardId())
                .heritageName(dto.getHeritageName())
                .imageUrl(problemImageS3Key)
                .objectImageUrl(objectImageS3Key)
                .description(dto.getDescription())
                .content(dto.getContent())
                .example1(dto.getExample1())
                .example2(dto.getExample2())
                .example3(dto.getExample3())
                .example4(dto.getExample4())
                .answer(dto.getAnswer())
                .build();

        problemMapper.insertHeritageProblem(heritageProblemsDto);
    }
}
