package am.itspace.townrestaurantsweb.serviceWeb;

import am.itspace.townrestaurantscommon.entity.User;
import am.itspace.townrestaurantscommon.entity.VerificationToken;

public interface VerificationTokenService {

    void delete(VerificationToken token);

    VerificationToken createToken(User user);

    VerificationToken findByPlainToken(String plainToken);
}

