package cn.filaura.weave.example.system.entity;



import cn.filaura.weave.annotation.Dict;
import cn.filaura.weave.annotation.Ref;
import cn.filaura.weave.example.consts.DictCodes;
import cn.filaura.weave.example.consts.DictColumns;
import cn.filaura.weave.example.consts.TableNames;
import cn.filaura.weave.example.consts.UserColumns;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class DictItem {

    private Long id;

    @Ref(table = TableNames.DICT, columns = DictColumns.NAME)
    private Long dictId;
    private String dictIdRefName;

    private String label;

    private String value;

    private Integer orderNum;

    private String description;

    @Dict(code = DictCodes.YN)
    private Integer enabled;
    private String enabledText;

    @Ref(table = TableNames.USER, columns = UserColumns.NICKNAME)
    private String createBy;
    private String createByRefNickname;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Ref(table = TableNames.USER, columns = UserColumns.NICKNAME)
    private String updateBy;
    private String updateByRefNickname;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
