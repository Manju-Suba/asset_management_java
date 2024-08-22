package assetmanagement.repository.disposed;

import assetmanagement.model.disposed.DisposedDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DisposedDetailRepository  extends MongoRepository<DisposedDetail,String> {
    List<DisposedDetail> findAllByStatus(String status);
}
