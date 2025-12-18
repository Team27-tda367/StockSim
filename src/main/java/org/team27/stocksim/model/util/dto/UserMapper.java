package org.team27.stocksim.model.util.dto;

import org.team27.stocksim.model.users.User;

public class UserMapper {

    // Convert Instrument to InstrumentDTO

    public static UserDTO toDto(User user, PortfolioDTO portfolio, OrderHistoryDTO orderHistory) {

        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getDisplayName());
        dto.setPortfolio(portfolio);
        dto.setOrderHistory(orderHistory);

        return dto;
    }

}
