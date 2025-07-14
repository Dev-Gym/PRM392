using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess.IRepostory;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess
{
    public class HealthArticleRepository : IHealthArticleRepository
    {
        private readonly AppDbContext _context;
        public HealthArticleRepository(AppDbContext context)
        {
            _context = context;
        }

        public void deleteById(int id) {
            var item = _context.HealthArticles.FirstOrDefault(x => x.ArticleId == id);
            _context.HealthArticles.Remove(item);   
            _context.SaveChanges();
        }

        public (List<HealthArticle>, int totalItem) findAllWithPagination(int pg, int pageSize)
        {
            var list = _context.HealthArticles
            .Include(h => h.Author)
            .OrderByDescending(h => h.ArticleId)
            .ToList();
            var total = list.Count();
            int skip = (pg-1)*pageSize;
            var data = list.Skip(skip).Take(pageSize).ToList();
            return (data, total);
        }

        public HealthArticle FindById(int id)
        {
            return _context.HealthArticles
                                 .Include(h => h.Author)
                          
                                 .FirstOrDefault(h => h.ArticleId == id);
        }

        public List<HealthArticle> GetAll()
        {
           return _context.HealthArticles
             .Include(h => h.Author)
            
             .ToList();
        }

        public void Save(HealthArticle healthArticle)
        {
           _context.HealthArticles.Add(healthArticle);
            _context.SaveChanges();
        }

        public void Udpate(HealthArticle healthArticle)
        {
            _context.Update(healthArticle);
            _context.SaveChanges(true); 
        }

    
      
    }
}
