package com.oil.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: OilSamplePageQueryDTO
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/15
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OilSamplePageQueryDTO implements Serializable {
    private String information;
    private String region;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;
    private Integer page;
    private Integer pageSize;
}
