packagee com.base.CRM.entity.dto;

public record LoginResponseDto(
    String acessToken,
    String tokenType,
    Long expiresIn
) {

}
