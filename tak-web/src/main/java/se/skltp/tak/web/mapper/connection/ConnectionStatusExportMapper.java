package se.skltp.tak.web.mapper.connection;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import se.skltp.tak.web.dto.connection.ConnectionStatus;
import se.skltp.tak.web.dto.connection.ConnectionStatusExport;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ConnectionStatusExportMapper {
    List<ConnectionStatusExport> toExportDto(List<ConnectionStatus> connectionStatuses);

    @Mapping(source="hsaId", target="serviceProducer")
    @Mapping(source="url", target="baseAddress")
    @Mapping(source="analysisResult.tlsProtocol", target="tlsProtocol")
    ConnectionStatusExport toExportDto(ConnectionStatus connectionStatus);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default String map(Optional<String> value) {
        return value.orElse("");
    }
}
