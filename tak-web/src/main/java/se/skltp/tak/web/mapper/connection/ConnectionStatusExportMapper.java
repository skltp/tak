package se.skltp.tak.web.mapper.connection;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import se.skltp.tak.web.dto.connection.ConnectionStatus;
import se.skltp.tak.web.dto.connection.ConnectionStatusExport;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConnectionStatusExportMapper {
    List<ConnectionStatusExport> toExportDto(List<ConnectionStatus> connectionStatuses);

    @Mapping(source="hsaId", target="serviceProducer")
    @Mapping(source="url", target="baseAddress")
    @Mapping(source="analysisResult.tlsProtocol", target="tlsProtocol", qualifiedByName = "nullToString")
    ConnectionStatusExport toExportDto(ConnectionStatus connectionStatus);

    @Named("nullToString")
    default String nullToString(String value) {
        return value != null ? value : "UNKNOWN";
    }
}
