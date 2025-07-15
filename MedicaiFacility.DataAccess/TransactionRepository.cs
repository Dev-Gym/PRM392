using MedicaiFacility.BusinessObject;
using MedicaiFacility.BusinessObject.Pagination;
using MedicaiFacility.DataAccess.IRepostory;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess
{
	public class TransactionRepository : ITransactionRepository
	{
		private readonly AppDbContext _Context;
		public TransactionRepository(AppDbContext Context)
		{
			_Context = Context;
		}

		public void DeleteById(int id)
		{
			var item = _Context.Transactions.FirstOrDefault(x => x.TransactionId == id);
			_Context.Transactions.Remove(item);
			_Context.SaveChanges();
		}

		public List<Transaction> GetAll()
		{
			return _Context.Transactions.OrderByDescending(t => t.TransactionId).Include(t => t.User).ToList();
		}

		public Transaction GetById(int id)
		{
			return _Context.Transactions.Include(x=>x.User).FirstOrDefault(x=>x.TransactionId==id);
		}

		public void Update(Transaction transaction)
		{
			_Context.Transactions.Update(transaction);
			_Context.SaveChanges(true);
		}

		public (List<Transaction>, int totalItems) GetListByPagination(int pg, int pageSize)
		{

			var list = _Context.Transactions.OrderByDescending(x=>x.TransactionId).Include(t => t.User).Include(x=>x.Balance).ToList();
			int total = list.Count();
			var pager = new Pager(total, pg, pageSize);
			int skipItem = (pg - 1) * pageSize;
			var data = list.Skip(skipItem).Take(pager.Pagesize).ToList();
			return (data, total);
		}

		public Transaction Create(Transaction transaction)
		{
			
			_Context.Transactions.Add(transaction);
			_Context.SaveChanges();
			return transaction;
		}

		public (List<Transaction>, int totalItems) GetListByPaginationWithPatientId(int pg, int pageSize,int patientId) {
			var list = _Context.Transactions.Where(x=>x.UserId== patientId).OrderByDescending(x => x.TransactionId).Include(t => t.User).Include(x => x.Balance).ToList();
			int total = list.Count();
			var pager = new Pager(total, pg, pageSize);
			int skipItem = (pg - 1) * pageSize;
			var data = list.Skip(skipItem).Take(pager.Pagesize).ToList();
			return (data, total);
		}
		
	}
}
