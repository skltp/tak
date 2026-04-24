/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
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
