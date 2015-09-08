require 'bigdecimal'

def rotateRight(bits, length, toRotate)
	actRotate = toRotate % length
	rotateLeft(bits, length, length - actRotate)
end

def rotateLeft(bits, length, toRotate)
	actRotate = toRotate % length
	result = 0;
	result |= bits << actRotate & ((1 << length) - 1)
	result |= bits >> (length - actRotate)
end

def presentsboxlayerq(ptext)
	temp = presentsboxlayer(ptext)
	result = 0;
	(0...63).each do |i|
		result |= ((temp & (1<<i)) >> i) << ((i * 16) % 63)
	end
	result |= temp & (1<<63)
end

def presentsboxlayer(ptext)
	result = ptext
	(0..15).each do |pos| 
		result = presentsboxq(result, pos)
		puts result.to_s(16)
	end
	result
end

def presentsboxq(ptext, pos)
	text = ptext & ~(0xf << pos*4)
	temp = (presentsbox((ptext >> (pos*4)) & 0xf)) 
	text |= temp << pos*4
end

def presentsbox(inp)
	sbox = [0xc, 0x5, 0x6, 0xb, 0x9, 0x0, 0xa, 0xd, 0x3, 0xe, 0xf, 0x8, 0x4, 0x7, 0x1, 0x2]
	sbox[inp]	
end

def calcBProb(q, n)
	num = BigDecimal.new(1)
	for i in (q-n+1..q)
		num = num.mult(BigDecimal.new(i),100)
	end
	puts num
	denom = BigDecimal.new(q)
	denom = denom**n
	puts num.div(denom,100).to_s('E')
end

def arkMDhashfunc(message, iv)
	cval = iv
	(0..(message.length-1)/2).each do |i|
		str = message[2*i,2]
		if str.length==1
			puts "Padding final block #{str} with 2"
			str[1] = '2'
		end
		puts "Message block #{i}: #{str}"
		cval = arkMDcompressionfunction(cval, str.to_i)
		puts "Chaining value: #{cval}"
	end
	cval
end

def arkMDcompressionfunction(h, m)
	val = (((h*100 + m + 193) ** 3) / 10000) % 100
end

def findCollision(val, iv)
	outpchaining = arkMDhashfunc(val, iv)
	count = (val.length-1)/2
	findCollision1(count, iv, outpchaining, "")
end

def findCollision1(count, iv, outpCV, message)
	puts "Count: #{count}, Output chaining value: #{outpCV}, Message = #{message}"
	for m in(0..99)
		if(count==0)
			if(arkMDcompressionfunction(iv, m)==outpCV)
				puts "Collision: #{sprintf("%02d",m) + message}"
				return sprintf("%02d",m) + message
			end 
		else
			for h in(0..99)				
				if(arkMDcompressionfunction(h, m)==outpCV)
					puts "h: #{h} m: #{m}"
					nmessage = findCollision1(count-1,iv,h, sprintf("%02d",m) + message)
					if(nmessage!=nil)
						return nmessage
					end
				end
			end
		end
	end
	nil
end
