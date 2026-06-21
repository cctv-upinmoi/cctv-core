package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.dto.request.JobRequest;
import init.upinmcse.cctvcore.exception.AppException;
import init.upinmcse.cctvcore.exception.ErrorCode;
import init.upinmcse.cctvcore.model.Job;
import init.upinmcse.cctvcore.repository.JobRepository;
import init.upinmcse.cctvcore.service.IJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService implements IJobService {

    private final JobRepository jobRepository;

    @Override
    @Transactional
    public Job create(JobRequest request) {
        return jobRepository.save(Job.builder()
                .name(request.getName())
                .cameraId(request.getCameraId())
                .cameraName(request.getCameraName())
                .alertTypes(request.getAlertTypes())
                .enabled(request.isEnabled())
                .build());
    }

    @Override
    @Transactional
    public Job update(String jobId, JobRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
        job.setName(request.getName());
        job.setCameraId(request.getCameraId());
        job.setCameraName(request.getCameraName());
        job.setAlertTypes(request.getAlertTypes());
        job.setEnabled(request.isEnabled());
        return jobRepository.save(job);
    }

    @Override
    @Transactional
    public void delete(String jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new AppException(ErrorCode.JOB_NOT_FOUND);
        }
        jobRepository.deleteById(jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getAll() {
        return jobRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Job getById(String jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
    }
}
