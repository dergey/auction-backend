package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.dao.BidDao;
import com.sergey.zhuravlev.auctionserver.dao.LotDao;
import com.sergey.zhuravlev.auctionserver.model.Lot;
import com.sergey.zhuravlev.auctionserver.model.User;
import com.sergey.zhuravlev.auctionserver.thread.LotsTreadLoader;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@Service
public class LotServiceImpl implements LotService {

    private static final Logger logger = LoggerFactory.getLogger(LotService.class);

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private SessionFactory sf;

    @Autowired
    private LotDao lotDao;

    @Autowired
    private BidDao bidDao;

    @Autowired
    private LotsTreadLoader lotsTreadLoader;

    @Override
    public List<Lot> getAll() {
        return lotDao.findAll();
    }

    @Override
    public Lot getByID(Long id) {
        Lot lot = lotDao.findOne(id);
        lot.setLastBid(bidDao.getBetByLotIdAndMaxSize(id));
        return lot;
    }

    @Override
    public List<Lot> getByCategoryID(Long categoryID) {
        return lotDao.getByCategory_Id(categoryID);
    }

    @Override
    public List<Lot> getByOwner(User user) {
        return getByOwnerID(user.getId());
    }

    @Override
    public List<Lot> getByOwnerID(Long ownerID) {
        return lotDao.getByOwner_Id(ownerID);
    }

    @Override
    public List<Lot> getByOwnerIDAndStatus(Long ownerID, Integer status) {
        return lotDao.getByOwnerIdAndStatus(ownerID, status);
    }

    @Override
    public List<Lot> getLotsByBuyerId(Long id) {
        return lotDao.getLotsByBuyer(id);
    }

    public List<Lot> getLots(Long categoryID, Long ownerID, String query, Integer offset){
        String q = "select l from Lot l";

        if (offset == null) offset = 0;

        if (categoryID != null) q = "select l from Lot l where l.category.id =" + categoryID; else
        if (ownerID != null) q = "select l from Lot l where l.owner.id = " + ownerID; else
        if (query != null) q = "select l from Lot l where l.title like %" + query + "%";

        sf = entityManagerFactory.unwrap(SessionFactory.class);
        final Transaction transaction = sf.getCurrentSession().beginTransaction();

        List<Lot> result = (List<Lot>) sf.getCurrentSession().createQuery(q)
                .setFirstResult(offset)
                .setMaxResults(5)
                .list();

        transaction.commit();
        return result;
    }

    //TODO Добавить ResponseEntity
    @Override
    public void save(Lot lot) {
        lotDao.saveAndFlush(lot);
        if (lotsTreadLoader.getTread().nearestLotIsNull() ||
                lot.getExpirationDate().getTime() < lotsTreadLoader.getTread().getCurrentNearestLot()) {
            lotsTreadLoader.start(lot);
        }
    }

    @Override
    public void remove(Long id) {
        if (lotsTreadLoader.getTread().getNearestLot().getId().longValue() == id.longValue()) {
            lotsTreadLoader.getTread().interrupt();
            lotDao.delete(id);
            try {
                lotsTreadLoader.foundNearestLot();
                lotsTreadLoader.getTread().start();
            } catch (Exception e) {
                logger.error("Found Nearest Lot Error: " + e.getLocalizedMessage());
            }
        } else lotDao.delete(id);
    }

    @Override
    public List<Lot> search(String query) {
        return lotDao.getLotsByTitleLike(query);
    }

    @Override
    public List<Lot> getPurchasedLots(Long id) {
        return lotDao.getPurchasedLots(id);
    }

    @Override
    public List<Lot> getRandom() {
        return lotDao.getRandom(5);
    }

    @Override
    public Lot getNearestExpirationDate() {
        return lotDao.getNearestExpirationDate();
    }

}
