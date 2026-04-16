package se.skltp.tak.web.entity;

import jakarta.persistence.*;

@Entity
public class Locktb {
	@Id
	@Column(name="tabell")
	private String id;

	private Integer locked;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public Integer getLocked() {
		return locked;
	}
	public void setLocked(Integer locked) {
		this.locked = locked;
	}
}
