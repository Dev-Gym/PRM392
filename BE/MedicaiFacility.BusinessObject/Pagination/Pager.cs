using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.BusinessObject.Pagination
{
    public class Pager
    {
        public int TotalItems { get; set; }
        public int CurrentPage { get; set; }

        public int Pagesize { get; set; }

        public int TotalPages { get; set; }

        public int StartPage { get; set; }

        public int EndPage { get; set; }

        public Pager()
        {

        }

        public Pager(int totalItems, int page, int pagesize)
        {
            int totalPages = (int)Math.Ceiling((Decimal)totalItems / (Decimal)pagesize);
            int currentPage = page;
            int startPage = currentPage - 5;
            int endPage = currentPage + 4;

            if (startPage <= 0)
            {

                endPage = endPage - (startPage - 1);
                startPage = 1;
            }

            if (endPage > totalPages)
            {
                endPage = totalPages;
                if (endPage > 10)
                {
                    startPage = endPage - 9;
                }
                TotalItems = totalItems;
                CurrentPage = currentPage;
                Pagesize = pagesize;
                TotalPages = totalPages;
                StartPage = startPage;
                EndPage = endPage;
            }

        }
    }
}
