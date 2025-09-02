package com.tanfed.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class JV_Array_Data {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private Double amount;
	
	@Column
	private String drOrCr;
	
	@Column
	private String mainHead;
	
	@Column
	private String subHead;
}
