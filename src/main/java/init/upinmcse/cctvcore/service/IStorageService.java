package init.upinmcse.cctvcore.service;

public interface IStorageService {
    public String upload(String key, byte[] data, String contentType);
    public byte[] download(String key);
    public void delete(String key);
}
