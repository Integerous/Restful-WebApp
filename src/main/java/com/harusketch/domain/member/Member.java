package com.harusketch.domain.member;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.harusketch.domain.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)//기본 생성자 자동 추가, 기본생성자의 접근권한을 protected로 제한
//Entity 클래스를 프로젝트 코드상에서 기본생성자로 생성하는 것은 막되, JPA에서 Entity 클래스를 생성하는 것은 허용하기 위해 추가
@Getter//클래스 내 모든 필드의 Getter 메소드 자동생성
@Entity//테이블과 링크될 클래스임을 나타냄, 언더스코어 네이밍으로 이름을 매칭
public class Member extends BaseTimeEntity{
		
		@Id//해당 테이블의 PK 필드 나타냄
		@GeneratedValue(strategy = GenerationType.IDENTITY)//PK의 생성 규칙을 나타냄.
		private Long emailId;
		private String pwd;
		private String name;
		private String address;
		private String phone;
		
		

		@Builder//해당 클래스의 빌더패턴 클래스를 생성. (생성자 상단에 선언시 생성자에 포함된 필드만 빌더에 포함)
		public Member(String name, String address, String phone) {
			this.name = name;
			this.address = address;
			this.phone = phone;
		}
		
}
