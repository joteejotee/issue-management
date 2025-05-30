package com.example.its.config;

import com.example.its.domain.issue.IssueStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IssueStatusTypeHandler extends BaseTypeHandler<IssueStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, IssueStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public IssueStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String status = rs.getString(columnName);
        return IssueStatus.fromString(status);
    }

    @Override
    public IssueStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String status = rs.getString(columnIndex);
        return IssueStatus.fromString(status);
    }

    @Override
    public IssueStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String status = cs.getString(columnIndex);
        return IssueStatus.fromString(status);
    }
} 