package init.upinmcse.cctvcore.web;

import init.upinmcse.cctvcore.common.AppResponse;
import init.upinmcse.cctvcore.dto.request.JobRequest;
import init.upinmcse.cctvcore.model.Job;
import init.upinmcse.cctvcore.security.ConfiguratorAccess;
import init.upinmcse.cctvcore.service.IJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ConfiguratorAccess
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final IJobService jobService;

    @GetMapping
    public AppResponse<List<Job>> getAll() {
        return AppResponse.success(jobService.getAll());
    }

    @GetMapping("/{id}")
    public AppResponse<Job> getById(@PathVariable String id) {
        return AppResponse.success(jobService.getById(id));
    }

    @PostMapping
    public AppResponse<Job> create(@RequestBody JobRequest request) {
        return AppResponse.success(jobService.create(request));
    }

    @PutMapping("/{id}")
    public AppResponse<Job> update(@PathVariable String id, @RequestBody JobRequest request) {
        return AppResponse.success(jobService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public AppResponse<Void> delete(@PathVariable String id) {
        jobService.delete(id);
        return AppResponse.success("Job deleted");
    }
}
