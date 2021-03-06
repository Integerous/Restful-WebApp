package com.harusketch.domain.board;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import com.harusketch.domain.BaseTimeEntity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
//@ToString
@Entity
public class Notice extends BaseTimeEntity{
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long noticeId;
	private String writer;
	private String title;
	private String content;
	
	private LocalDateTime createdDate;
	
	
	public Notice(String writer, String title, String content) {

		this.writer = writer;
		this.title = title;
		this.content = content;
		this.createdDate = LocalDateTime.now();
	}
	
	//날짜변환
	public String getFormattedCreatedDate() {
		if (createdDate == null) {
			return "";
		}
		return createdDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"));
	}

	
	
}
