package com.covoiturage.service.impl; import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.covoiturage.entity.Admin;
import com.covoiturage.entity.Conducteur;
import com.covoiturage.entity.Passager;
import com.covoiturage.repository.AdminRepository;
import com.covoiturage.repository.ConducteurRepository;
import com.covoiturage.repository.PassagerRepository;
import com.covoiturage.service.UserService;
 @Service public class UserServiceImpl implements UserService{
    private final ConducteurRepository c;private final PassagerRepository p;private final AdminRepository a;
    public UserServiceImpl(ConducteurRepository c,PassagerRepository p,AdminRepository a)
    {
        this.c=c;this.p=p;this.a=a;
    } 
    public List<Conducteur> conducteurs()
    {
        return c.findAll();
    } 
    public List<Passager> passagers(){
        return p.findAll();
    } 
    public List<Admin> admins(){
        return a.findAll();
    } 
    public Optional<Conducteur> conducteur(String cin)
    {
        return c.findById(cin);
    }
    public Optional<Passager> passager(String cin){
        return p.findById(cin);
    }
    public Optional<Admin> admin(String cin){
        return a.findById(cin);
    }
    public Admin defaultAdmin(){
        return a.findAll().stream().findFirst().orElseThrow(()->new IllegalStateException("Aucun administrateur configure"));
    }
    public String displayName(String role,String cin){
        if("conducteur".equals(role))
            return conducteur(cin).map(x->x.getPrenom()+" "+x.getNom()).orElse(cin);
        if("passager".equals(role))
            return passager(cin).map(x->x.getPrenom()+" "+x.getNom()).orElse(cin);
        return admin(cin).map(x->x.getPrenom()+" "+x.getNom()).orElse(cin);
    }
    @Transactional
    public void deleteUser(String role,String cin){
        if("conducteur".equals(role))
            c.deleteById(cin);
        else if("passager".equals(role))
            p.deleteById(cin);
    }
    @Transactional
    public void updateCard(String role,String cin,String carte){
        if("conducteur".equals(role))
            c.findById(cin).ifPresent(u->{
                u.setCarte(carte);
                c.save(u);
            });
        else
            p.findById(cin).ifPresent(u->{
                u.setCarte(carte);
                p.save(u);
            });
    }
}
