package com.pet.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色和菜单关联 sys_role_menu
 *
 * @author zy
 */

@Data
@NoArgsConstructor
@TableName("sys_role_menu")
public class SysRoleMenu {

    /**
     * 角色ID
     */
    @TableId(type = IdType.INPUT)
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long menuId;

}
