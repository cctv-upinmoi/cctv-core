package init.upinmcse.cctvcore.service;

import init.upinmcse.cctvcore.dto.request.JobRequest;
import init.upinmcse.cctvcore.model.Job;

import java.util.List;

public interface IJobService {
    Job create(JobRequest request);
    Job update(String jobId, JobRequest request);
    void delete(String jobId);
    List<Job> getAll();
    Job getById(String jobId);
}
