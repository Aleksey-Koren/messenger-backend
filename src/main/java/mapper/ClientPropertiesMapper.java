package mapper;

import com.example.whisper.dto.ClientPropertiesDto;
import com.example.whisper.entity.Utility;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.whisper.entity.Utility.Key.MESSAGE_LIFESPAN;

@Service
public class ClientPropertiesMapper {

    public ClientPropertiesDto toDto(List<Utility> utilities) {
        Map<String, String> utilMap = utilities.stream().collect(Collectors.toMap(Utility::getUtilKey, Utility::getUtilValue));

        return ClientPropertiesDto.builder()
                .messageLifespan(utilMap.get(MESSAGE_LIFESPAN.name()) != null ? Long.valueOf(utilMap.get(MESSAGE_LIFESPAN.name())) : null)
                .build();

    }
}