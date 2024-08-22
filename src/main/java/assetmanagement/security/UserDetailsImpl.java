package assetmanagement.security;

import java.util.Collection;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import assetmanagement.model.Users;


import java.util.Collections;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;
    private String id;
    private String userId;
    private String companyId;
    private String fullName;
    private String phoneNo;
    private String email;
    private String role;
    private String profilePicture;
    private String pictureWithPath;
    @JsonIgnore
    private String password;
    private String city;
    private String plant;
    private String domain;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String id, String userId,String companyId, String fullName, String phoneNo, String email, String password,
            String city, String domain, String role,String plant,String profilePicture,String pictureWithPath,
            Collection<? extends GrantedAuthority> authorities) {
        super();
        this.id = id;
        this.userId = userId;
        this.companyId = companyId;
        this.fullName = fullName;
        this.phoneNo = phoneNo;
        this.email = email;
        this.password = password;
        this.city = city;
        this.profilePicture = profilePicture;
        this.pictureWithPath = pictureWithPath;
        this.role = role;
        this.plant = plant;
        this.domain = domain;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Users user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
        // GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        return new UserDetailsImpl(
                user.getId(),
                user.getUserId(),
                user.getCompanyId(),
                user.getFullName(),
                user.getPhoneNo(),
                user.getEmail(),
                user.getPassword(),
                user.getCity(),
                user.getDomain(),
                user.getRole(),
                user.getPlant(),
                user.getPictureWithPath(),
                user.getProfilePicture(),
                authorities);
    }

    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getCity() {
        return city;
    }
    public String getPlant() {
        return plant;
    }

    public String getDomain() {
        return domain;
    }

    public String getRole() {
        return role;
    }

    public String getPhoneNo() {
        return phoneNo;
    }
    
    public String getFullName() {
        return fullName;
    }

    public String getPictureWithPath() {
        return "/uploads/"+pictureWithPath;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // public GrantedAuthority getAuthority() {
    //     return authority;
    // }

	@Override
	public String getUsername() {
		return null;
	}

}

