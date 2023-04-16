<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${backendPackage}.infrastructure.db.dao.${table.className}Dao">

    <select id="page" resultType="${backendPackage}.infrastructure.db.po.${table.className}">
        select a.* from ${table.name} a
        <where>
        </where>
        order by a.create_at desc
    </select>
</mapper>
