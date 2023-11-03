package ru.mtuci.MindScape.user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Table(name = "moderator")
public class Moderator extends BaseUser {

}