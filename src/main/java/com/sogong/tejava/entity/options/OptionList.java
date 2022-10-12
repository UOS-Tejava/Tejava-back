package com.sogong.tejava.entity.options;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sogong.tejava.entity.style.StyleItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "option_list")
public class OptionList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToMany(mappedBy = "optionList")
    private List<OptionsItem> optionsItem = new ArrayList<>();
}
