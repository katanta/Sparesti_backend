package org.ntnu.idi.idatt2106.sparesti.sparestibackend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yubico.webauthn.data.ByteArray;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import lombok.*;
import org.hibernate.annotations.SortNatural;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.util.ByteArrayAttributeConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Represents a user of the system, encapsulating all personal and authentication details.
 * This class implements the {@link UserDetails} interface from Spring Security to integrate
 * authentication and authorization functionalities seamlessly.
 *
 * @author L.M.L Nilsen, H.L Xu nad Y.A Marouga
 * @Entity Marks this class as a JPA entity mapped to a database table named "USER".
 * @Builder Implements the builder pattern for this class using Lombok.
 * @NoArgsConstructor Generates a protected no-argument constructor needed for JPA.
 * @AllArgsConstructor Generates a constructor with arguments for all fields.
 * @Getter Lombok annotation to generate getters for all fields.
 * @Setter Lombok annotation to generate setters for applicable fields.
 * @Table Specifies the table name and implies further configuration like unique constraints.
 */
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "\"USER\"")
public class User implements UserDetails {

    /**
     * The unique identifier for the user, automatically generated by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    /**
     * First name of the user; cannot be null.
     */
    @NotNull
    @Column(nullable = false)
    private String firstName;

    /**
     * Last name of the user; cannot be null.
     */
    @NotNull
    @Column(nullable = false)
    private String lastName;

    /**
     * Unique username for the user; cannot be null.
     */
    @NotNull
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Encrypted password for user authentication; cannot be null.
     */
    @NotNull
    @Column(nullable = false)
    private String password;

    /**
     * User's email address, must be valid as per the @Email constraint; unique across users.
     */
    @NotNull
    @Column(nullable = false, unique = true)
    @Email
    private String email;

    /**
     * Timestamp indicating when the user's current streak of activity started.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime streakStart;

    /**
     * Current streak count, reflecting consecutive days of user activity or other measured streaks.
     */
    @NotNull
    @Column(nullable = false)
    private Long streak;

    /**
     * Total amount saved by the user across all challenges and goals.
     */
    @NotNull
    @Column(nullable = false)
    private BigDecimal savedAmount;

    /**
     * Configuration details for this user, embedded within the user record.
     */
    @Setter @Embedded private UserConfig userConfig;

    /**
     * Collection of goals set by the user, sorted naturally by priority.
     */
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @SortNatural
    @JsonManagedReference
    @Setter(AccessLevel.NONE)
    private final Set<Goal> goals = new TreeSet<>();

    /**
     * Set of challenges associated with the user, loaded eagerly.
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private final Set<Challenge> challenges = new HashSet<>();

    /**
     * Account details specifically for the user's spending activities.
     */
    @AttributeOverride(name = "accNumber", column = @Column(name = "spending_acc_number"))
    @AttributeOverride(name = "balance", column = @Column(name = "spending_balance"))
    private Account spendingAccount = new Account();

    /**
     * Account details specifically for the user's savings.
     */
    @AttributeOverride(name = "accNumber", column = @Column(name = "saving_acc_number"))
    @AttributeOverride(name = "balance", column = @Column(name = "saving_balance"))
    private Account savingAccount = new Account();

    /**
     * Collection of badges awarded to the user, representing achievements or milestones.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_BADGE",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "BADGE_ID"))
    @Setter(AccessLevel.NONE)
    private final Set<Badge> badges = new HashSet<>();

    /**
     * Binary handle used for certain types of user identification or authentication processes.
     */
    @Column(length = 64)
    @Lob
    @Convert(converter = ByteArrayAttributeConverter.class)
    private ByteArray handle;

    /**
     * Gets authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userConfig.getRole().name()));
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
}
