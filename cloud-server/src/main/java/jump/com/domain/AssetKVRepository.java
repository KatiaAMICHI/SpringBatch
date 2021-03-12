package jump.com.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetKVRepository extends JpaRepository<Asset, Integer> {
    Asset getByLabel(final String label);
}
