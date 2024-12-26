package com.tsspdcl.sas.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="sasusers")

public class User {
    @SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @SequenceGenerator(initialValue=1, name="user_id_seq", sequenceName="user_sequence", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="user_id_seq")
    private Long id;

    @Column(nullable=false)
    private String sasofficeadd;

    @Column(nullable=false, unique=true)
    private String sasusername;
    
    @Column(nullable=false)
    private String sasdesg;
    
    @Column(nullable=false)
    private String sasseccd;
    
    @Column(nullable=false)
    private String sasusertype;

    @Column(nullable=false)
    private String password;
    
    @Column(nullable=false)
    private String sascircd;
    
    @Column(nullable=false)
    private String saserono;
        
    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
   
    @JoinTable(
    	name="users_roles",
        joinColumns={@JoinColumn(name="user_id", referencedColumnName="id")},
        inverseJoinColumns={@JoinColumn(name="role_id", referencedColumnName="id")})
    
    private List<Role> roles = new ArrayList<>();

	@Override
	public String toString() {
		return "User [id=" + id + ", sasofficeadd=" + sasofficeadd + ", sasusername=" + sasusername + ", sasdesg="
				+ sasdesg + ", sasseccd=" + sasseccd + ", sasusertype=" + sasusertype + ", password=" + password
				+ ", roles=" + roles + ", sascircd=" + sascircd + ", saserocd=" + saserono + "]";
	}
}
